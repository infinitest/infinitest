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

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WhenFilteringUnnecessaryClassesFromStackTraces
{
    private StackTraceFilter filter;

    @Before
    public void inContext()
    {
        filter = new StackTraceFilter();
    }

    @Test
    public void shouldRemoveInfinitestRunnerClasses()
    {
        assertEquals(emptyList(), filter.filterStack(newArrayList(element("org.infinitest.runner.Foobar"))));
    }

    @Test
    public void shouldNotRemoveRegularInfinitestClasses()
    {
        assertFalse(filter.filterStack(newArrayList(element("org.infinitest.Foobar"))).isEmpty());
    }

    @Test
    public void shouldRemoveJUnitClasses()
    {
        assertEquals(emptyList(), filter.filterStack(newArrayList(element("org.junit.Foobar"),
                        element("junit.framework.Foobar"))));
    }

    @Test
    public void shouldRemoveSunReflectionClasses()
    {
        assertEquals(emptyList(), filter.filterStack(newArrayList(element("sun.reflect.Foo"),
                        element("java.lang.reflect.Method"))));
    }

    private StackTraceElement element(String classname)
    {
        return new StackTraceElement(classname, "someMethod", "someFile", 0);
    }
}
