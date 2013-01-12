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

import com.epickrram.tool.tracker.Config;
import com.epickrram.tool.tracker.client.gui.View;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public final class MulticastReceiverFactory
{
    MulticastReceiver create()
    {
        try
        {
            final InetAddress address = InetAddress.getByName(Config.getMulticastAddress());
            final MulticastSocket multicastSocket = new MulticastSocket(Config.getMulticastPort());
            final NetworkInterface multicastInterface = Config.getMulticastInterface();
            if(multicastInterface != null)
            {
                multicastSocket.setInterface(multicastInterface.getInetAddresses().nextElement());
            }
            multicastSocket.joinGroup(address);
            return new MulticastReceiver(new View(), multicastSocket);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to create multicast socket", e);
        }
    }
}
