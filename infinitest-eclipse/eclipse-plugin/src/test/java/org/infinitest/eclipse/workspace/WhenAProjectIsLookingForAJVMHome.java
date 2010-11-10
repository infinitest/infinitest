package org.infinitest.eclipse.workspace;

import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;
import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.Test;

public class WhenAProjectIsLookingForAJVMHome
{
    @Test
    public void shouldFallBackToSystemPropertyIfJvmHomeCannotBeFound() throws CoreException
    {
        IJavaProject project = project("projectA");
        ProjectFacade facade = new ProjectFacade(project);
        assertEquals(new File(System.getProperty("java.home")), facade.getJvmHome());
    }
}
