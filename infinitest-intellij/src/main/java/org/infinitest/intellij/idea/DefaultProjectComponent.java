package org.infinitest.intellij.idea;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ProjectComponent;

public abstract class DefaultProjectComponent implements ProjectComponent
{
    @NotNull
    public String getComponentName()
    {
        return getClass().getName();
    }

    public void projectOpened()
    {
    }

    public void projectClosed()
    {
    }

    public void initComponent()
    {
    }

    public void disposeComponent()
    {
    }
}
