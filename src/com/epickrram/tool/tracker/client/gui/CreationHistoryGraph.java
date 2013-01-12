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

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public final class CreationHistoryGraph extends Canvas
{
    private static final Color TEXT_COLOUR = Color.BLACK;
    private static final Color BORDER_COLOUR = Color.BLACK;
    private static final Color LINE_COLOUR = Color.BLUE;
    private static final int BORDER_WIDTH = 5;
    private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 16);
    private static final Stroke STROKE = new BasicStroke(2);
    private static final Color BACKGROUND_COLOUR = Color.WHITE;

    private final int width;
    private final int height;
    private final Map<Object, Object> renderingHints = new HashMap<Object, Object>();
    private Image buffer;
    private ClientObjectStats clientObjectStats;

    public CreationHistoryGraph(final int width, final int height)
    {
        this.width = width;
        this.height = height;
        setBackground(BACKGROUND_COLOUR);
        setSize(width, height);
    }

    @Override
    public void paint(final Graphics g)
    {
        if (buffer == null)
        {
            buffer = createImage(width, height);
            final Graphics2D graphics = (Graphics2D) buffer.getGraphics();
            final Map<Object, Object> hints = graphics.getRenderingHints();
            renderingHints.putAll(hints);
            renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            renderingHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        }
        draw((Graphics2D) buffer.getGraphics());
        g.drawImage(buffer, 0, 0, this);
    }

    private void draw(final Graphics2D g)
    {
        g.setRenderingHints(renderingHints);
        g.clearRect(0, 0, width, height);
        drawBorder(g);

        if (clientObjectStats != null)
        {
            final int[] creationRateHistory = clientObjectStats.getCreationRateHistory();
            final int historyLength = creationRateHistory.length;
            final int columnWidth = (int) ((width - (2 * BORDER_WIDTH)) / (float) (historyLength - 2));

            int minCreationRate = Integer.MAX_VALUE;
            int maxCreationRate = Integer.MIN_VALUE;

            for (int i = 0; i < historyLength; i++)
            {
                final int rate = creationRateHistory[i];
                if (rate > maxCreationRate)
                {
                    maxCreationRate = rate;
                }
                if (rate < minCreationRate)
                {
                    minCreationRate = rate;
                }
            }

            final int creationRateRange = maxCreationRate - minCreationRate;
            final float stepSize = (height - (2 * BORDER_WIDTH)) / (float) creationRateRange;

            g.setColor(LINE_COLOUR);
            for (int i = 0, n = historyLength - 1, last = historyLength - 2; i < n; i++)
            {
                final int columnHeightLeft = creationRateHistory[i] - minCreationRate;
                final int columnHeightRight = creationRateHistory[i + 1] - minCreationRate;
                final int displayColumnHeightLeft = (int) (columnHeightLeft * stepSize);
                final int displayColumnHeightRight = (int) (columnHeightRight * stepSize);

                final int x1 = BORDER_WIDTH + (i * columnWidth);
                final int y1 = height - BORDER_WIDTH - displayColumnHeightLeft;
                final int x2 = x1 + columnWidth;
                final int y2 = height - BORDER_WIDTH - displayColumnHeightRight;
                g.setStroke(STROKE);
                g.drawLine(x1, y1, x2, y2);

                if (i == last)
                {
                    drawCreationRateLabel(g, creationRateHistory[i + 1]);
                }
            }
        }
    }

    private void drawCreationRateLabel(final Graphics2D g, final int count)
    {
        final FontMetrics fontMetrics = g.getFontMetrics(FONT);
        final String text = Integer.toString(count);
        final Rectangle2D stringBounds = fontMetrics.getStringBounds(text, g);

        g.setColor(BACKGROUND_COLOUR);
        g.fillRect(width - BORDER_WIDTH - (int) stringBounds.getWidth(),
                BORDER_WIDTH,
                (int) stringBounds.getWidth(), (int) stringBounds.getHeight());
        g.setColor(TEXT_COLOUR);
        g.setFont(FONT);
        g.drawString(text,
                width - BORDER_WIDTH - (int) stringBounds.getWidth(),
                BORDER_WIDTH + (int) stringBounds.getHeight() - 2);
    }

    private void drawBorder(final Graphics g)
    {
        g.setColor(BORDER_COLOUR);
        g.drawRect(0, 0, width - 1, height - 1);
    }

    public void onData(final ClientObjectStats clientObjectStats)
    {
        this.clientObjectStats = clientObjectStats;
        repaint();
    }

    @Override
    public void update(final Graphics g)
    {
        paint(g);
    }
}