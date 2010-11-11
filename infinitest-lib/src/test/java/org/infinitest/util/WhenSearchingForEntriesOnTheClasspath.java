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
package org.infinitest.util;

import static org.hamcrest.Matchers.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import org.infinitest.InfinitestCore;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

public class WhenSearchingForEntriesOnTheClasspath
{
    private String systemClasspath;

    @Before
    public void inContext()
    {
        systemClasspath = systemClasspath();
    }

    @Test
    public void shouldFindJarsThatContainAClass()
    {
        String entry = findClasspathEntryFor(systemClasspath, Iterables.class);
        assertThat(entry, containsString(".jar"));
        assertThat(entry, containsString("google"));
    }

    @Test
    public void shouldFindClassDirectoriesThatContainAClass()
    {
        String entry = findClasspathEntryFor(systemClasspath, InfinitestCore.class);
        assertThat(entry, not(containsString(".jar")));
        assertThat(entry, containsString("target/classes"));
    }
}
