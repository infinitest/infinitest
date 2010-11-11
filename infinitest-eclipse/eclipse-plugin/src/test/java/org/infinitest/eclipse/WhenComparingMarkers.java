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
package org.infinitest.eclipse;

import static org.hamcrest.CoreMatchers.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import org.infinitest.eclipse.markers.ProblemMarkerInfo;
import org.infinitest.eclipse.workspace.FakeResourceFinder;
import org.junit.Before;
import org.junit.Test;

public class WhenComparingMarkers
{
    private Throwable error;
    private ProblemMarkerInfo methodError;
    private ProblemMarkerInfo methodFailure;
    private FakeResourceFinder finder;
    private AssertionError failure;

    @Before
    public void inContext()
    {
        error = new Throwable();
        failure = new AssertionError();
        failure.fillInStackTrace();
        finder = new FakeResourceFinder();
        methodError = new ProblemMarkerInfo(methodFailed("testClass", "", error), finder);
        methodFailure = new ProblemMarkerInfo(methodFailed("message", "testClass", "methodName", failure), finder);
    }

    @Test
    public void shouldBeEqualIfTestNameAndMethodNameAreEqual()
    {
        assertEquals(methodError, new ProblemMarkerInfo(methodFailed("testClass", "", error), finder));
        assertThat(methodError, not(equalTo(new ProblemMarkerInfo(methodFailed("testClass2", "", error), finder))));

        ProblemMarkerInfo errorMarker = new ProblemMarkerInfo(methodFailed("testClass", "", new AssertionError()),
                        finder);
        assertThat(methodError, equalTo(errorMarker));
        assertThat(methodFailure, not(equalTo(methodError)));
    }
}
