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
package org.infinitest.eclipse.resolution;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.infinitest.eclipse.util.PickleJar.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.junit.Before;
import org.junit.Test;

public class WhenDisplayingTestFailureDetails
{
    protected Collection<StackTraceElement> actualStackTrace;
    private ErrorViewerResolution resolution;
    protected String actualMessage;

    @Before
    public void inContext()
    {
        actualStackTrace = newArrayList();
        resolution = new ErrorViewerResolution("TestName.methodName")
        {
            @Override
            protected void createStackViewWith(List<StackTraceElement> trace, String message)
            {
                actualStackTrace = trace;
                actualMessage = message;
            }
        };
    }

    @Test
    public void shouldUseTestAndMethodNameInLabel()
    {
        assertEquals("TestName.methodName failing (see details)", resolution.getLabel());
    }

    @Test
    public void printStackTraceWithSourceFileLinksUsingInternalJavaStackTraceConsole() throws Exception
    {
        IMarker marker = createMock(IMarker.class);
        StackTraceElement element = new StackTraceElement("", "", "", 0);
        Object pickledStackTrace = pickle(new StackTraceElement[] { element });
        expect(marker.getAttribute(PICKLED_STACK_TRACE_ATTRIBUTE)).andReturn(pickledStackTrace);
        expect(marker.getAttribute(MESSAGE)).andReturn("message");
        replay(marker);

        resolution.run(marker);
        assertEquals(element, getOnlyElement(actualStackTrace));
        assertEquals("message", actualMessage);

        verify(marker);
    }
}
