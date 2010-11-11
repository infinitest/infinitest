/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.testrunner;

import static java.lang.System.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.EventSupport.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WhenCalculatingTestStatistics
{
    private RunStatistics statistics;

    @Before
    public void inContext()
    {
        statistics = new RunStatistics();
    }

    @Test
    public void shouldProvideLastFailureTime()
    {
        statistics.testCaseComplete(testCaseFailing("test1", "", new Throwable()));
        assertThat(currentTimeMillis() - statistics.getLastFailureTime("test1"), lessThan(10l));
    }

    @Test
    public void shouldReturnZeroForTestsThatHaveNeverFailed()
    {
        assertEquals(0, statistics.getLastFailureTime("UnknownTest"));
    }

    @Test
    public void shouldTreatErrorsLikeFailures()
    {
        statistics.testCaseComplete(testCaseFailing("test1", "", new Exception()));
        assertThat(currentTimeMillis() - statistics.getLastFailureTime("test1"), lessThan(10l));
    }
}
