package org.infinitest.util;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.*;
import static org.infinitest.util.CollectionUtils.*;
import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

public class WhenRetrievingFirstElementFromCollection
{
    @Test
    public void shouldReturnFirstElementFromList()
    {
        assertThat(first(asList("foo", "bar", "baz")), is("foo"));
    }

    @Test
    public void shouldReturnAnyElementFromSet()
    {
        assertThat(first(singleton("foo")), is("foo"));
    }

    @Test
    public void shouldReturnNullForEmptySet()
    {
        assertThat(first(new HashSet<Object>()), is(nullValue()));
    }
}
