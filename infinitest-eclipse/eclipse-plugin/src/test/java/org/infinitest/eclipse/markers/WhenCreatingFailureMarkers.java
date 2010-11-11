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
package org.infinitest.eclipse.markers;

import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.infinitest.eclipse.util.PickleJar.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import org.infinitest.eclipse.workspace.FakeResourceFinder;
import org.junit.Test;

public class WhenCreatingFailureMarkers
{
    @Test
    public void shouldStorePickledStackTraces()
    {
        Throwable throwable = new Throwable();
        throwable.fillInStackTrace();
        ProblemMarkerInfo info = new ProblemMarkerInfo(methodFailed("testName", "methodName", throwable),
                        new FakeResourceFinder());

        String pickledStackTrace = info.attributes().get(PICKLED_STACK_TRACE_ATTRIBUTE).toString();
        StackTraceElement[] stackTrace = (StackTraceElement[]) unpickle(pickledStackTrace);
        assertEquals(throwable.getStackTrace().length, stackTrace.length);
    }
}
