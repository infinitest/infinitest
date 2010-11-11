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
