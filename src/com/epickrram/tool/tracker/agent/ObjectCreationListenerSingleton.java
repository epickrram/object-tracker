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

import com.epickrram.tool.disruptor.ProxyMethodInvocation;
import com.epickrram.tool.disruptor.RingBufferProxyEventFactory;
import com.epickrram.tool.disruptor.RingBufferProxyGenerator;
import com.epickrram.tool.disruptor.RingBufferProxyGeneratorFactory;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.epickrram.tool.disruptor.GeneratorType.BYTECODE_GENERATION;

public final class ObjectCreationListenerSingleton
{
    public static final ObjectCreationListener INSTANCE = createInstance();

    private static ObjectCreationListener createInstance()
    {
        final ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());
        final Disruptor<ProxyMethodInvocation> disruptor =
                new Disruptor<ProxyMethodInvocation>(new RingBufferProxyEventFactory(), 32768, executor);
        final RingBufferProxyGenerator proxyGenerator =
                new RingBufferProxyGeneratorFactory().create(BYTECODE_GENERATION);
        final ObjectCreationListener objectCreationListener =
                proxyGenerator.createRingBufferProxy(new InstanceListener(),
                        ObjectCreationListener.class, disruptor);

        addShutdownHook(executor, disruptor);
        disruptor.start();

        return new ObjectCreationListenerMBean(objectCreationListener);
    }

    private ObjectCreationListenerSingleton() {}

    private static void addShutdownHook(final ExecutorService executor, final Disruptor<ProxyMethodInvocation> disruptor)
    {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            public void run()
            {
                disruptor.shutdown();
                executor.shutdown();
            }
        }));
    }

    private static final class DaemonThreadFactory implements ThreadFactory
    {
        public Thread newThread(final Runnable runnable)
        {
            final Thread thread = new Thread(runnable);
            thread.setName("daemon-object-tracker-processor");
            return thread;
        }
    }
}
