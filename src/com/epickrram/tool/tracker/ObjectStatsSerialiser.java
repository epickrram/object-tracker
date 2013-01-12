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


package com.epickrram.tool.tracker;

import com.epickrram.tool.tracker.agent.ObjectStats;
import com.epickrram.tool.tracker.client.ClientObjectStats;

public final class ObjectStatsSerialiser
{
    public static void writeTo(final Class<?> cls,
                               final ObjectStats objectStats,
                               final DataBuffer writeable)
    {
        writeable.reset();
        writeable.writeString(cls.getSimpleName());
        writeable.writeLong(objectStats.getTotalCreatedCount());
        writeable.writeInt(objectStats.getMaximumCreationRate());

        final int[] creationRateHistory = objectStats.getCreationRateHistory();

        int ptr = creationRateHistory.length - 1;
        final int availableIntegerSpace = (writeable.remaining() / DataBuffer.SIZE_OF_INT) - 1;
        final int copiedHistoryLength = Math.min(availableIntegerSpace, creationRateHistory.length);

        writeable.writeInt(copiedHistoryLength);

        for(int i = 0; i < copiedHistoryLength && i > -1; i++)
        {
            writeable.writeInt(creationRateHistory[ptr--]);
        }
    }

    public static ClientObjectStats readFrom(final DataBuffer readable)
    {
        final String classSimpleName = readable.readString();
        final long totalCreatedCount = readable.readLong();
        final int maximumCreationRate = readable.readInt();
        final int historyLength = readable.readInt();

        final int[] creationRateHistory = new int[historyLength];
        for(int i = historyLength; i != 0; i--)
        {
            creationRateHistory[i - 1] = readable.readInt();
        }

        return new ClientObjectStats(classSimpleName, totalCreatedCount, maximumCreationRate, creationRateHistory);
    }
}
