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

import static org.infinitest.intellij.idea.facet.ConfigurationElementBuilder.*;
import static org.junit.Assert.*;

import org.infinitest.intellij.idea.facet.InfinitestFacetConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.intellij.openapi.util.InvalidDataException;

public class WhenReadingConfiguration
{
    private InfinitestFacetConfiguration configuration;

    @Before
    public void inContext()
    {
        configuration = new InfinitestFacetConfiguration();
    }

    @Test
    public void shouldReadScmEnabledSetting() throws InvalidDataException
    {
        configuration.readExternal(configuration().withScmUpdate(true).build());
        assertTrue(configuration.isScmUpdateEnabled());
    }

    @Test
    public void shouldShouldReadScmDisabledSettings() throws InvalidDataException
    {
        configuration.readExternal(configuration().withScmUpdate(false).build());
        assertFalse(configuration.isScmUpdateEnabled());
    }

    @Test
    public void shouldTreatScmAsDisabledIfNotDefined() throws InvalidDataException
    {
        configuration.readExternal(configuration().build());
        assertFalse(configuration.isScmUpdateEnabled());
    }
}
