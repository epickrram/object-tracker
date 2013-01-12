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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ThreadSafeObjectCreationListener implements ObjectCreationListener
{
    private final ObjectCreationListener delegate;
    private final Lock lock = new ReentrantLock();
    private final Operation<String> dumpObjectCreationCountsOperation = new DumpObjectCreationCountsOperation();
    private final Operation<Class<?>> onObjectCreationOperation = new OnObjectCreationOperation();
    private final Operation<Void> updateStatsOperation = new UpdateStatsOperation();

    public ThreadSafeObjectCreationListener(final ObjectCreationListener delegate)
    {
        this.delegate = delegate;
    }

    public void onObjectCreation(final Class<?> cls)
    {
        doInLock(cls, onObjectCreationOperation);
    }

    public void updateStats()
    {
        doInLock(null, updateStatsOperation);
    }

    public void dumpObjectCreationCounts(final String filename)
    {
        doInLock(filename, dumpObjectCreationCountsOperation);
    }

    private <T> void doInLock(final T value, final Operation<T> operation)
    {
        lock.lock();
        try
        {
            operation.execute(value);
        }
        finally
        {
            lock.unlock();
        }
    }

    private interface Operation<T>
    {
        void execute(final T value);
    }

    private final class DumpObjectCreationCountsOperation implements Operation<String>
    {
        public void execute(final String value)
        {
            delegate.dumpObjectCreationCounts(value);
        }
    }

    private final class OnObjectCreationOperation implements Operation<Class<?>>
    {
        public void execute(final Class<?> value)
        {
            delegate.onObjectCreation(value);
        }
    }

    private final class UpdateStatsOperation implements Operation<Void>
    {
        public void execute(final Void value)
        {
            delegate.updateStats();
        }
    }
}