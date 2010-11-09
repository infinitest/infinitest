package org.infinitest.eclipse.util;

import org.eclipse.core.runtime.IPath;

public abstract class PathUtils
{
    public static String absPath(IPath outputLocation)
    {
        return outputLocation.toFile().getAbsoluteFile().getAbsolutePath();
    }
}
