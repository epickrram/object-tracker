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
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public final class ObjectStatsPublisherTest
{
    @Test
    public void shouldPublishObjectStats() throws Exception
    {
        final DataBuffer writable = DataBuffer.writable(256);
        final CapturingDataPublisher dataPublisher = new CapturingDataPublisher();

        final ObjectStats objectStats = new ObjectStats(64);
        for(int i = 0; i < 64; i++)
        {
            objectStats.update(i * i, TimeUnit.SECONDS.toNanos(1L) * (i + 1));
        }

        new ObjectStatsPublisher(dataPublisher, writable).publish(String.class, objectStats);

        assertThat(dataPublisher.getData(), is(notNullValue()));
    }

    private static final class CapturingDataPublisher implements DataPublisher
    {
        private byte[] data;

        public void publish(final byte[] data)
        {
            this.data = data;
        }

        public byte[] getData()
        {
            return data;
        }
    }
}
