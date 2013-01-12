//////////////////////////////////////////////////////////////////////////////////
//   Copyright 2011   Mark Price     mark at epickrram.com                      //
//                                                                              //
//   Licensed under the Apache License, Version 2.0 (the "License");            //
//   you may not use this file except in compliance with the License.           //
//   You may obtain a copy of the License at                                    //
//                                                                              //
//       http://www.apache.org/licenses/LICENSE-2.0                             //
//                                                                              //
//   Unless required by applicable law or agreed to in writing, software        //
//   distributed under the License is distributed on an "AS IS" BASIS,          //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   //
//   See the License for the specific language governing permissions and        //
//   limitations under the License.                                             //
//////////////////////////////////////////////////////////////////////////////////


package com.epickrram.tool.tracker.client.gui;

import com.epickrram.tool.tracker.client.ClientObjectStats;
import com.epickrram.tool.tracker.client.ObjectStatsSubscriber;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class View extends JFrame implements ObjectStatsSubscriber
{
    private static final ClientObjectStatsComparator STATS_COMPARATOR = new ClientObjectStatsComparator();
    private static final String[] COLUMN_NAMES = new String[]{"class", "created count", "rate"};
    public static final int HISTORY_GRAPH_WIDTH = 200;
    public static final int HISTORY_GRAPH_HEIGHT = 80;

    private final Map<String, CreationHistoryGraph> dataRowByObjectName = new HashMap<String, CreationHistoryGraph>();
    private final Map<String, ClientObjectStats> objectStatsByClassName = new HashMap<String, ClientObjectStats>();

    private final JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;

    public View() throws HeadlessException
    {
        setSize(700, 600);
        setVisible(true);
        final ScrollPane scrollPane = new ScrollPane();
        panel = new JPanel(new FlowLayout());

        scrollPane.add(panel);
        getContentPane().add(scrollPane);

        initialise();

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable()
        {
            public void run()
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        update();
                    }
                });
            }
        }, 5L, 2L, TimeUnit.SECONDS);
    }

    private void initialise()
    {
        tableModel = new DefaultTableModel(new Object[0][], COLUMN_NAMES);
        final DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        columnModel.addColumn(new TableColumn(0, 300, new DefaultTableCellRenderer(), null));
        columnModel.addColumn(new TableColumn(1, 100, new DefaultTableCellRenderer(), null));
        columnModel.addColumn(new TableColumn(2, 300, new ComponentTableCellRenderer(), null));
        table = new JTable(tableModel, columnModel);
        table.setRowHeight(HISTORY_GRAPH_HEIGHT);
        panel.add(table);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private final class ComponentTableCellRenderer implements TableCellRenderer
    {

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column)
        {
            return (Component) View.this.tableModel.getValueAt(row, column);
        }
    }

    public void onObjectStats(final ClientObjectStats stats)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                objectStatsByClassName.put(stats.getClassSimpleName(), stats);
            }
        });
    }

    private void update()
    {
        final java.util.List<ClientObjectStats> snapshot = new ArrayList<ClientObjectStats>(objectStatsByClassName.values());
        Collections.sort(snapshot, STATS_COMPARATOR);
        final int dataCount = snapshot.size();
        final Object[][] data = new Object[dataCount][3];
        for(int i = 0; i < dataCount; i++)
        {
            final ClientObjectStats stats = snapshot.get(i);
            data[i][0] = stats.getClassSimpleName();
            data[i][1] = stats.getTotalCreatedCount();
            final CreationHistoryGraph graph = getGraph(stats);
            data[i][2] = graph;
            graph.onData(stats);
        }

        tableModel.setDataVector(data, COLUMN_NAMES);
    }

    private CreationHistoryGraph getGraph(final ClientObjectStats clientObjectStats)
    {
        CreationHistoryGraph graph;
        final String classSimpleName = clientObjectStats.getClassSimpleName();
        if(dataRowByObjectName.containsKey(classSimpleName))
        {
            graph = dataRowByObjectName.get(classSimpleName);
        }
        else
        {
            graph = new CreationHistoryGraph(HISTORY_GRAPH_WIDTH, HISTORY_GRAPH_HEIGHT);
            dataRowByObjectName.put(classSimpleName, graph);
        }
        return graph;
    }

    private static class ClientObjectStatsComparator implements Comparator<ClientObjectStats>
    {
        public int compare(ClientObjectStats o1, ClientObjectStats o2)
        {
            final int[] o2CreationRateHistory = o2.getCreationRateHistory();
            final int[] o1CreationRateHistory = o1.getCreationRateHistory();
            return Integer.valueOf(o2CreationRateHistory[o2CreationRateHistory.length - 1]).
                    compareTo(o1CreationRateHistory[o1CreationRateHistory.length - 1]);
        }
    }
}
