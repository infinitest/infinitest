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
