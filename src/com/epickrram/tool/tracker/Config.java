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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Logger;

public final class Config
{
    public static final String MULTICAST_ADDRESS_PROPERTY_NAME = "com.epickrram.tool.tracker.multicast.address";
    public static final String MULTICAST_PORT_PROPERTY_NAME = "com.epickrram.tool.tracker.multicast.port";

    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
    private static final String MULTICAST_ADDRESS_DEFAULT = "239.0.37.1";
    private static final int MULTICAST_PORT_DEFAULT = 14400;

    public static String getMulticastAddress()
    {
        return System.getProperty(MULTICAST_ADDRESS_PROPERTY_NAME, MULTICAST_ADDRESS_DEFAULT);
    }

    public static int getMulticastPort()
    {
        return Integer.getInteger(MULTICAST_PORT_PROPERTY_NAME, MULTICAST_PORT_DEFAULT);
    }

    public static NetworkInterface getMulticastInterface() throws SocketException
    {
        NetworkInterface multicastInterface = null;
        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while(networkInterfaces.hasMoreElements())
        {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();
            if(networkInterface.isUp() && networkInterface.supportsMulticast())
            {
                multicastInterface = networkInterface;
                LOGGER.info(String.format("Using interface %s for multicast traffic", multicastInterface.getDisplayName()));
                break;
            }
        }
        return multicastInterface;
    }
}
