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
package org.infinitest.intellij;

import static org.hamcrest.Matchers.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.infinitest.util.CollectionUtils.*;
import static org.junit.Assert.*;

import org.infinitest.intellij.idea.language.IdeaInfinitestAnnotator;
import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class WhenAnnotatingClass
{
    private IdeaInfinitestAnnotator annotator;

    @Before
    public void setUp()
    {
        annotator = IdeaInfinitestAnnotator.getInstance();
    }

    @Test
    public void shouldAnnotateTopLevelClass()
    {
        annotator.annotate(eventWithError(new Exception()));
        assertThat(first(annotator.getTestEvents()).getPointOfFailureClassName(), is(getClass().getName()));
    }

    @Test
    public void shouldAnnotateContainingClassOfInnerClassFailures()
    {
        annotator.annotate(eventWithError(InnerClass.createException()));
        assertThat(first(annotator.getTestEvents()).getPointOfFailureClassName(), is(getClass().getName()));
    }

    static class InnerClass
    {
        public static Throwable createException()
        {
            try
            {
                fail("Intentional exception");
            }
            catch (Throwable e)
            {
                return e;
            }

            return null;
        }
    }

    private static TestEvent eventWithError(Throwable error)
    {
        return new TestEvent(METHOD_FAILURE, "", "", "", error);
    }
}
