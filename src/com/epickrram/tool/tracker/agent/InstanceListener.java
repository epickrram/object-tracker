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


package com.epickrram.tool.tracker.agent;

import com.epickrram.tool.tracker.DataBuffer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class InstanceListener implements ObjectCreationListener
{
    private static final Logger LOGGER = Logger.getLogger(InstanceListener.class.getName());
    private static final int OBJECT_STATS_HISTORY_LENGTH = 32;

    private final Map<Class<?>, Long> objectCreateCounterMap = new HashMap<Class<?>, Long>();
    private final Map<Class<?>, ObjectStats> objectStatsMap = new HashMap<Class<?>, ObjectStats>();
    private final ObjectStatsPublisher statsPublisher;

    InstanceListener()
    {
        final MulticastDataPublisher publisher = new MulticastDataPublisherFactory().create();
        statsPublisher = new ObjectStatsPublisher(publisher, DataBuffer.writable(1024));
    }
	
	public void onObjectCreation(final Class<?> cls)
	{
        if(objectCreateCounterMap.containsKey(cls))
		{
			final Long currentCount = objectCreateCounterMap.get(cls);
			objectCreateCounterMap.put(cls, Long.valueOf(currentCount.longValue() + 1));
		}
		else
		{
			objectCreateCounterMap.put(cls, Long.valueOf(1));
            objectStatsMap.put(cls, new ObjectStats(OBJECT_STATS_HISTORY_LENGTH));
		}
	}

    public void updateStats()
    {
        final long timestamp = System.nanoTime();
        for(final Class<?> cls : objectCreateCounterMap.keySet())
        {
            final ObjectStats objectStats = objectStatsMap.get(cls);
            objectStats.update(objectCreateCounterMap.get(cls), timestamp);
            statsPublisher.publish(cls, objectStats);
        }
    }

    public void dumpObjectCreationCounts(final String filename)
    {
        try
        {
            final PrintWriter writer = new PrintWriter(new FileWriter(filename));
            writer.printf("classname,count,max_rate_per_sec");
            for(int i = 0; i < OBJECT_STATS_HISTORY_LENGTH; i++)
            {
                writer.printf(",rate_%d", i);
            }
            writer.print('\n');
            for (Class<?> cls : objectCreateCounterMap.keySet())
            {
                final ObjectStats objectStats = objectStatsMap.get(cls);
                writer.printf("%s,%d,%d", cls.getName(),
                        objectStats.getTotalCreatedCount(),
                        objectStats.getMaximumCreationRate());
                final int[] creationRateHistory = objectStats.getCreationRateHistory();
                for(int i = 0, n = creationRateHistory.length; i < n; i++)
                {
                    writer.printf(",%d", creationRateHistory[i]);
                }
                writer.print('\n');
            }
            writer.close();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Failed to write file", e);
        }
    }
}