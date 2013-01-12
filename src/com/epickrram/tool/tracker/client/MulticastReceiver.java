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


package com.epickrram.tool.tracker.client;

import com.epickrram.tool.tracker.DataBuffer;
import com.epickrram.tool.tracker.ObjectStatsSerialiser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MulticastReceiver implements Runnable
{
    private static final Logger LOGGER = Logger.getLogger(MulticastReceiver.class.getName());
    private static final int MAX_MESSAGE_SIZE = 1500;

    private final ObjectStatsSubscriber objectStatsSubscriber;
    private final MulticastSocket receiverSocket;
    private final DatagramPacket datagramPacket;
    private final DataBuffer dataBuffer;

    public MulticastReceiver(final ObjectStatsSubscriber objectStatsSubscriber,
                             final MulticastSocket receiverSocket)
    {
        this.objectStatsSubscriber = objectStatsSubscriber;
        this.receiverSocket = receiverSocket;
        datagramPacket = new DatagramPacket(new byte[MAX_MESSAGE_SIZE], 0, MAX_MESSAGE_SIZE);
        dataBuffer = DataBuffer.readable(new byte[MAX_MESSAGE_SIZE]);
    }

    void start(final ExecutorService executorService)
    {
        executorService.submit(this);
    }

    public void run()
    {
        while(!Thread.currentThread().isInterrupted())
        {
            try
            {
                receiverSocket.receive(datagramPacket);
                dataBuffer.readFrom(datagramPacket.getData());

                final ClientObjectStats clientObjectStats = ObjectStatsSerialiser.readFrom(dataBuffer);
                objectStatsSubscriber.onObjectStats(clientObjectStats);
            }
            catch (IOException e)
            {
                LOGGER.log(Level.WARNING, "Failed to receive/parse message", e);
            }
        }
    }
}