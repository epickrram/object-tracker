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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MulticastDataPublisher implements DataPublisher
{
    private static final Logger LOGGER = Logger.getLogger(MulticastDataPublisher.class.getName());

    private final MulticastSocket senderSocket;
    private final DatagramPacket datagramPacket;
    private boolean lastSendFailed = false;

    public MulticastDataPublisher(final MulticastSocket senderSocket, final DatagramPacket datagramPacket)
    {
        this.senderSocket = senderSocket;
        this.datagramPacket = datagramPacket;
    }

    public void publish(final byte[] data)
    {
        datagramPacket.setData(data);

        try
        {
            senderSocket.send(datagramPacket);
            lastSendFailed = false;
        }
        catch (IOException e)
        {
            if(!lastSendFailed)
            {
                LOGGER.log(Level.WARNING, "Failed to send message", e);
            }
            lastSendFailed = true;
        }
    }
}
