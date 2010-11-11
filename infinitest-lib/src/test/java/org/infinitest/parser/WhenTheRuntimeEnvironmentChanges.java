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
package org.infinitest.parser;

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.fakeco.fakeproduct.id.FakeId;

public class WhenTheRuntimeEnvironmentChanges extends DependencyGraphTestBase
{
    @Test
    public void shouldRecreateTheClassFileIndex()
    {
        addToDependencyGraph(FakeId.class);
        assertEquals(1, getGraph().getIndexedClasses().size());

        getGraph().setClasspathProvider(emptyClasspath());
        assertEquals(0, getGraph().getIndexedClasses().size());
    }
}
