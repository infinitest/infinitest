package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Lists.*;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

public class JavaProjectSetTest
{
    private IJavaProject project;
    private JavaProjectSet projectSet;

    @Before
    public void inContext()
    {
        URL resource = getClass().getResource("/emptyJar.jar");
        File externalJar = new File(resource.getPath());
        String externalJarPath = externalJar.getAbsolutePath();

        project = project("projectA").withJar("lib/someJarFile").andExportedJar("lib/someExportedJarFile")
                        .andExtenralJar(externalJarPath).andSourceDirectory("src", "srcbin").andDependsOn("projectB");
        ResourceFinder finder = mock(ResourceFinder.class);
        List<IJavaProject> projects = newArrayList(project);
        when(finder.getJavaProjects()).thenReturn(projects);
        projectSet = new JavaProjectSet(finder);
    }

    @Test
    public void shouldProvideOutputDirectoriesForProjectAsAbsolutePaths() throws JavaModelException
    {
        ArrayList<File> expectedDirectories = newArrayList(new File("/root/projectA/srcbin"), new File(
                        "/root/projectA/target/classes"));

        assertEquals(expectedDirectories, projectSet.outputDirectories(new ProjectFacade(project)));
    }
}
