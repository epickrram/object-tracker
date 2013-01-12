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
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class ObjectStatsSerialiserTest
{
    private ObjectStats objectStats;

    @Before
    public void setup()
    {
        objectStats = new ObjectStats(64);

        for(int i = 0; i < 64; i++)
        {
            objectStats.update(i * i, TimeUnit.SECONDS.toNanos(1L) * (i + 1));
        }
    }

    @Test
    public void shouldSerialiseObjectStats() throws Exception
    {
        final DataBuffer writable = DataBuffer.writable(256);

        ObjectStatsSerialiser.writeTo(String.class, objectStats, writable);
        final byte[] buffer = new byte[256];
        writable.copyTo(buffer);

        final DataBuffer readable = DataBuffer.readable(buffer);
        assertThat(readable.readString(), is(String.class.getSimpleName()));
        assertThat(readable.readLong(), is(63L * 63L));
        assertThat(readable.readInt(), is(125));
        assertThat(readable.readInt(), is(56));

        int expected = 125;
        for(int i = 0; i < 56; i++)
        {
            assertThat(readable.readInt(), is(expected));
            expected -= 2;
        }
    }

    @Test
    public void shouldDeserialiseObjectStats() throws Exception
    {
        final DataBuffer writable = DataBuffer.writable(256);

        ObjectStatsSerialiser.writeTo(String.class, objectStats, writable);
        final byte[] buffer = new byte[256];
        writable.copyTo(buffer);

        final ClientObjectStats stats = ObjectStatsSerialiser.readFrom(DataBuffer.readable(buffer));

        assertThat(stats.getTotalCreatedCount(), is(63L * 63L));
        assertThat(stats.getMaximumCreationRate(), is(125));
        final int[] creationRateHistory = stats.getCreationRateHistory();
        assertThat(creationRateHistory.length, is(56));

        int expected = 15;
        for(int i = 0; i < 56; i++)
        {
            assertThat(creationRateHistory[i], is(expected));
            expected += 2;
        }
    }
}
