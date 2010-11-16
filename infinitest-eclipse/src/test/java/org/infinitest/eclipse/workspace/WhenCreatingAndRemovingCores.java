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
package org.infinitest.eclipse.workspace;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.infinitest.InfinitestCore;
import org.infinitest.eclipse.CoreLifecycleListener;
import org.junit.Before;
import org.junit.Test;

public class WhenCreatingAndRemovingCores implements CoreLifecycleListener
{
    private InfinitestCoreRegistry registry;
    private InfinitestCore coreAdded;
    private InfinitestCore coreRemoved;

    @Before
    public void inContext()
    {
        registry = new InfinitestCoreRegistry();
    }

    @Test
    public void shouldTolerateRemovingACoreThatsNotThere() throws URISyntaxException
    {
        registry.removeCore(new URI("//thisIsNotAProject"));
    }

    @Test
    public void shouldFireEventWhenCoresAreCreatedOrRemoved() throws URISyntaxException
    {
        InfinitestCore mockCore = createNiceMock(InfinitestCore.class);
        registry.addLifecycleListener(this);
        registry.addCore(new URI("//someProject"), mockCore);
        assertSame(coreAdded, mockCore);

        registry.removeCore(new URI("//someProject"));
        assertSame(coreRemoved, mockCore);
    }

    public void coreCreated(InfinitestCore core)
    {
        coreAdded = core;
    }

    public void coreRemoved(InfinitestCore core)
    {
        coreRemoved = core;
    }
}
