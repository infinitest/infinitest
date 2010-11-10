package org.infinitest.changedetect;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class WhenLookingForClassFiles
{
    private ClassFileFilter filter;

    @Before
    public void inContext()
    {
        filter = new ClassFileFilter();
    }

    @Test
    public void shouldIngnoreCase()
    {
        assertTrue(filter.accept(new File("foo.ClAsS")));
    }

    @Test
    public void shouldOnlyFindFilesWithClassExtension()
    {
        assertFalse(filter.accept(new File("foo.clas")));
        assertFalse(filter.accept(new File("fooclass")));
    }
}
