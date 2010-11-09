package org.infinitest.eclipse.workspace;

import static org.easymock.EasyMock.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public abstract class JavaProjectTestSupport
{
    public static final String PATH_TO_WORKSPACE = "/path/to/workspace";

    public static void expectProjectLocationFor(IJavaProject project, String projectName) throws JavaModelException
    {
        IResource projectResource = createMock(IResource.class);
        expect(projectResource.getLocation()).andReturn(new Path(PATH_TO_WORKSPACE + projectName)).anyTimes();
        expect(project.getCorrespondingResource()).andReturn(projectResource).anyTimes();
        replay(projectResource);
    }

    public static void outputLocationExpectation(IJavaProject project, String baseDir) throws JavaModelException
    {
        expect(project.getOutputLocation()).andReturn(new Path(baseDir + "/target/classes/")).anyTimes();
    }

    public static void expectClasspathFor(IJavaProject project, IClasspathEntry... entries) throws JavaModelException
    {
        expect(project.getResolvedClasspath(true)).andReturn(entries).anyTimes();
    }

    public static IJavaProject createMockProject(String projectPath, IClasspathEntry... entries)
                    throws JavaModelException, URISyntaxException
    {
        IJavaProject javaProject = createMock(IJavaProject.class);
        IProject project = createMock(IProject.class);
        expect(project.getLocationURI()).andReturn(new URI(projectPath));
        replay(project);
        expect(javaProject.getProject()).andReturn(project);
        expect(javaProject.getPath()).andReturn(new Path(projectPath)).anyTimes();
        expectProjectLocationFor(javaProject, projectPath);
        expectClasspathFor(javaProject, entries);
        outputLocationExpectation(javaProject, projectPath);
        return javaProject;
    }
}
