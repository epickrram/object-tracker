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

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class ObjectStatsTest
{
    private static final int HISTORY_LENGTH = 4;
    
    private ObjectStats stats;

    @Before
    public void setUp() throws Exception
    {
        stats = new ObjectStats(HISTORY_LENGTH);
    }

    @Test
    public void shouldDiscardFirstResult() throws Exception
    {
        stats.update(1, 1L);

        assertThat(stats.getMaximumCreationRate(), is(0));
        assertThat(stats.getTotalCreatedCount(), is(1L));
        assertArray(new int[0], stats.getCreationRateHistory());
    }

    @Test
    public void shouldTrackMaximumCreationRate() throws Exception
    {
        stats.update(1, 1L);

        stats.update(11, 1L + TimeUnit.SECONDS.toNanos(1L));
        assertThat(stats.getMaximumCreationRate(), is(10));

        stats.update(31, 1L + TimeUnit.SECONDS.toNanos(2L));
        assertThat(stats.getMaximumCreationRate(), is(20));

        stats.update(50, 1L + TimeUnit.SECONDS.toNanos(3L));
        assertThat(stats.getMaximumCreationRate(), is(20));

        stats.update(80, 1L + TimeUnit.SECONDS.toNanos(4L));
        assertThat(stats.getMaximumCreationRate(), is(30));
    }

    @Test
    public void shouldTrackCreationRateHistory() throws Exception
    {
        stats.update(1, 1L);

        stats.update(11, 1L + TimeUnit.SECONDS.toNanos(1L));
        assertArray(new int[]{10}, stats.getCreationRateHistory());

        stats.update(31, 1L + TimeUnit.SECONDS.toNanos(2L));
        assertArray(new int[]{10, 20}, stats.getCreationRateHistory());

        stats.update(50, 1L + TimeUnit.SECONDS.toNanos(3L));
        assertArray(new int[]{10, 20, 19}, stats.getCreationRateHistory());

        stats.update(80, 1L + TimeUnit.SECONDS.toNanos(4L));
        assertArray(new int[]{10, 20, 19, 30}, stats.getCreationRateHistory());

        stats.update(90, 1L + TimeUnit.SECONDS.toNanos(5L));
        assertArray(new int[]{20, 19, 30, 10}, stats.getCreationRateHistory());

        stats.update(95, 1L + TimeUnit.SECONDS.toNanos(6L));
        assertArray(new int[]{19, 30, 10, 5}, stats.getCreationRateHistory());
    }

    private static void assertArray(final int[] expected, final int[] actual)
    {
        assertThat(actual.length, is(expected.length));

        for (int i = 0; i < expected.length; i++)
        {
            assertThat(actual[i], is(expected[i]));
        }
    }
}
