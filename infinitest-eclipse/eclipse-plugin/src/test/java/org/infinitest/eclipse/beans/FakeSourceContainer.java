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
