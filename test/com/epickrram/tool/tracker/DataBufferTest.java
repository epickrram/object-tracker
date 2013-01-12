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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class DataBufferTest
{
    @Test
    public void shouldSerialiseIntValues() throws Exception
    {
        final DataBuffer writable = DataBuffer.writable(16);
        writable.writeInt(17);
        writable.writeInt(19);
        writable.writeInt(37);
        writable.writeInt(Integer.MAX_VALUE);

        final byte[] buffer = new byte[16];
        writable.copyTo(buffer);

        final DataBuffer readable = DataBuffer.readable(buffer);

        assertThat(readable.readInt(), is(17));
        assertThat(readable.readInt(), is(19));
        assertThat(readable.readInt(), is(37));
        assertThat(readable.readInt(), is(Integer.MAX_VALUE));
    }

    @Test
    public void shouldSerialiseLongValues() throws Exception
    {
        final DataBuffer writable = DataBuffer.writable(16);
        writable.writeLong(-12398749834L);
        writable.writeLong(Long.MAX_VALUE);

        final byte[] buffer = new byte[16];
        writable.copyTo(buffer);

        final DataBuffer readable = DataBuffer.readable(buffer);

        assertThat(readable.readLong(), is(-12398749834L));
        assertThat(readable.readLong(), is(Long.MAX_VALUE));
    }

    @Test
    public void shouldSerialiseStringValues() throws Exception
    {
        final DataBuffer writable = DataBuffer.writable(16);
        writable.writeString("abcd");

        final byte[] buffer = new byte[16];
        writable.copyTo(buffer);

        final DataBuffer readable = DataBuffer.readable(buffer);

        assertThat(readable.readString(), is("abcd"));
    }
}
