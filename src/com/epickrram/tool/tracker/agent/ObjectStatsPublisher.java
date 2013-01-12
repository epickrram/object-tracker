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
import com.epickrram.tool.tracker.ObjectStatsSerialiser;

public final class ObjectStatsPublisher
{
    private final DataPublisher dataPublisher;
    private final DataBuffer dataBuffer;
    private final byte[] copyBuffer;

    public ObjectStatsPublisher(final DataPublisher dataPublisher, final DataBuffer dataBuffer)
    {
        this.dataPublisher = dataPublisher;
        this.dataBuffer = dataBuffer;
        copyBuffer = new byte[dataBuffer.getLength()];
    }

    void publish(final Class<?> cls, final ObjectStats objectStats)
    {
        ObjectStatsSerialiser.writeTo(cls, objectStats, dataBuffer);
        
        dataBuffer.copyTo(copyBuffer);
        dataPublisher.publish(copyBuffer);
    }
}