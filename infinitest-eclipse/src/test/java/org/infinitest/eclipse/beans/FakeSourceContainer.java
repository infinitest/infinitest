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
package org.infinitest.eclipse.beans;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;

public class FakeSourceContainer implements ISourceContainer
{

    public void dispose()
    {

        throw new UnsupportedOperationException();
    }

    public Object[] findSourceElements(String arg0) throws CoreException
    {

        throw new UnsupportedOperationException();
    }

    public String getName()
    {

        throw new UnsupportedOperationException();
    }

    public ISourceContainer[] getSourceContainers() throws CoreException
    {

        throw new UnsupportedOperationException();
    }

    public ISourceContainerType getType()
    {

        throw new UnsupportedOperationException();
    }

    public void init(ISourceLookupDirector arg0)
    {

        throw new UnsupportedOperationException();
    }

    public boolean isComposite()
    {

        throw new UnsupportedOperationException();
    }

    public Object getAdapter(Class arg0)
    {

        throw new UnsupportedOperationException();
    }

}
