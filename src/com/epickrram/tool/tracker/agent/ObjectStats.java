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

import java.util.concurrent.TimeUnit;

public final class ObjectStats
{
    private final int historyLength;
    private final long arrayMask;
    private final int[] creationRateHistory;
    
    private long lastUpdateTimestampNanos = 0L;
    private long totalCreatedCount = 0L;
    private long historyIndex = 0;
    private int maxCreationRate = 0;

    public ObjectStats(final int historyLengthAsPowerOfTwo)
    {
        this.historyLength = historyLengthAsPowerOfTwo;
        arrayMask = historyLengthAsPowerOfTwo - 1;
        creationRateHistory = new int[historyLengthAsPowerOfTwo];
    }

    public void update(final long creationCount, final long timestampNanos)
    {
        if(lastUpdateTimestampNanos != 0L)
        {
            final long durationNanos = timestampNanos - lastUpdateTimestampNanos;
            final double durationSeconds = durationNanos / (double) TimeUnit.SECONDS.toNanos(1L);
            final int creationRate = (int)((creationCount - totalCreatedCount) / durationSeconds);
            creationRateHistory[(int) (historyIndex++ & arrayMask)] = creationRate;

            if(creationRate > maxCreationRate)
            {
                maxCreationRate = creationRate;
            }
        }

        lastUpdateTimestampNanos = timestampNanos;
        totalCreatedCount = creationCount;
    }

    public int getMaximumCreationRate()
    {
        return maxCreationRate;
    }

    public long getTotalCreatedCount()
    {
        return totalCreatedCount;
    }

    public int[] getCreationRateHistory()
    {
        if(historyIndex == 0)
        {
            return new int[0];
        }
        if(historyIndex < historyLength)
        {
            final int[] ascendingHistory = new int[(int)historyIndex];
            System.arraycopy(creationRateHistory, 0, ascendingHistory, 0, (int)historyIndex);
            return ascendingHistory;
        }
        else
        {
            final int[] ascendingHistory = new int[historyLength];
            long pointer = historyIndex;
            for(int i = 0; i < historyLength; i++)
            {
                ascendingHistory[i] = creationRateHistory[(int)(pointer++ & arrayMask)];
            }
            return ascendingHistory;
        }
    }
}