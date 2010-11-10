package org.infinitest.eclipse.resolution;

import org.eclipse.swt.widgets.Shell;

class FakeShell extends Shell
{
    boolean opened;
    boolean active;
    boolean packed;
    boolean layout;
    boolean disposed;

    @Override
    protected void checkSubclass()
    {
    }

    @Override
    public void open()
    {
        opened = true;
    }

    @Override
    public void forceActive()
    {
        active = true;
    }

    @Override
    public void pack()
    {
        packed = true;
    }

    @Override
    public void layout()
    {
        layout = true;
    }

    @Override
    public void dispose()
    {
        disposed = true;
    }
}