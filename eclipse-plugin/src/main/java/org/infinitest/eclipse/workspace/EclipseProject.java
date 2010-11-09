package org.infinitest.eclipse.workspace;

import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;

public interface EclipseProject
{
    URI getLocationURI();

    IPath getDefaultOutputLocation();

    IClasspathEntry[] getClasspathEntries();
}