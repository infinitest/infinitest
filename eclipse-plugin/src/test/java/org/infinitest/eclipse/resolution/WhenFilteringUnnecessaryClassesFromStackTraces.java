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
