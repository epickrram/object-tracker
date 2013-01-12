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

public final class ClientObjectStats
{
    private final String classSimpleName;
    private final int[] creationRateHistory;
    private final long totalCreatedCount;
    private final int maximumCreationRate;

    public ClientObjectStats(final String classSimpleName, final long totalCreatedCount, final int maximumCreationRate, final int[] creationRateHistory)
    {
        this.creationRateHistory = creationRateHistory;
        this.totalCreatedCount = totalCreatedCount;
        this.maximumCreationRate = maximumCreationRate;
        this.classSimpleName = classSimpleName;
    }

    public String getClassSimpleName()
    {
        return classSimpleName;
    }

    public int getMaximumCreationRate()
    {
        return maximumCreationRate;
    }

    public long getTotalCreatedCount()
    {
        return totalCreatedCount;
    }

    public int[] getCreationRateHistory()
    {
        return creationRateHistory;
    }
}
