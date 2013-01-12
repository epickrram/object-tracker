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

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class DataBuffer
{
    public static final int SIZE_OF_INT = 4;
    public static final int SIZE_OF_LONG = 8;
    
    private final int length;
    private final byte[] data;
    private final Unsafe unsafe;
    private final long byteArrayBaseOffset;

    private int position;

    private DataBuffer(final int length)
    {
        this(new byte[length]);
    }

    private DataBuffer(final byte[] data)
    {
        this.data = data;
        this.length = data.length;
        unsafe = getUnsafe();
        byteArrayBaseOffset = unsafe.arrayBaseOffset(byte[].class);
    }

    public static DataBuffer writable(final int length)
    {
        return new DataBuffer(length);
    }

    public static DataBuffer readable(final byte[] input)
    {
        return new DataBuffer(input);
    }

    public void writeInt(final int value)
    {
        checkCapacity(SIZE_OF_INT);
        unsafe.putInt(data, byteArrayBaseOffset + position, value);
        position += SIZE_OF_INT;
    }

    public void writeLong(final long value)
    {
        checkCapacity(SIZE_OF_LONG);
        unsafe.putLong(data, byteArrayBaseOffset + position, value);
        position += SIZE_OF_LONG;
    }

    public void writeString(final String value)
    {
        writeInt(value.length());
        writeBytes(value.getBytes());
    }

    public void writeBytes(final byte[] value)
    {
        checkCapacity(SIZE_OF_INT + value.length);
        writeInt(value.length);
        unsafe.copyMemory(value, byteArrayBaseOffset, data, byteArrayBaseOffset + position, value.length);
        position += value.length;
    }

    public int readInt()
    {
        final int value = unsafe.getInt(data, byteArrayBaseOffset + position);
        position += SIZE_OF_INT;
        return value;
    }

    public long readLong()
    {
        final long value = unsafe.getLong(data, byteArrayBaseOffset + position);
        position += SIZE_OF_LONG;
        return value;
    }

    public String readString()
    {
        final int length = readInt();
        final byte[] buffer = new byte[length];
        readBytes(buffer);

        return new String(buffer);
    }

    public void readBytes(final byte[] destination)
    {
        final int length = readInt();
        unsafe.copyMemory(data, byteArrayBaseOffset + position, destination, byteArrayBaseOffset, length);
        position += length;
    }

    public void copyTo(final byte[] buffer)
    {
        System.arraycopy(data, 0, buffer, 0, length);
    }

    public void readFrom(final byte[] buffer)
    {
        if(buffer.length > length)
        {
            throw new IllegalArgumentException(String.format("Supplied data too large. %d > %d.", buffer.length, length));
        }
        System.arraycopy(buffer, 0, data, 0, buffer.length);
        position = 0;
    }

    public int remaining()
    {
        return length - position;
    }

    public void reset()
    {
        position = 0;
    }

    public int getLength()
    {
        return length;
    }

    private void checkCapacity(final int dataSize)
    {
        if(position + dataSize > length)
        {
            throw new IllegalArgumentException("Capacity exceeded");
        }
    }

    private Unsafe getUnsafe()
    {
        try
        {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        }
        catch (ReflectiveOperationException e)
        {
            throw new IllegalStateException("Could not get Unsafe", e);
        }
    }
}
