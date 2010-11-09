package org.infinitest.eclipse.workspace;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

public class WhenExtractingSettingsFromAJavaProject
{
    private ProjectFacade facade;
    private IJavaProject project;
    private IPath path;

    @Before
    public void inContext() throws JavaModelException
    {
        project = mock(IJavaProject.class);
        path = mock(IPath.class);
        when(project.getOutputLocation()).thenReturn(path);
        facade = new ProjectFacade(project);
    }

    @Test
    public void shouldProvideDefaultOutputDirectory()
    {
        assertSame(path, facade.getDefaultOutputLocation());
    }
}
