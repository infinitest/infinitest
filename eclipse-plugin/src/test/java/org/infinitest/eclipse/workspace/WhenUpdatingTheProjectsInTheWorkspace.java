package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.infinitest.eclipse.util.StatusMatchers.equalsStatus;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.project;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.findingTests;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.noTestsRun;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.infinitest.ControlledEventQueue;
import org.infinitest.InfinitestCore;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.eclipse.ResourceEventSupport;
import org.infinitest.eclipse.UpdateListener;
import org.infinitest.eclipse.status.WorkspaceStatus;
import org.infinitest.eclipse.status.WorkspaceStatusListener;
import org.junit.Before;
import org.junit.Test;

public class WhenUpdatingTheProjectsInTheWorkspace extends ResourceEventSupport
{
    private List<ProjectFacade> projects;
    private CoreRegistry coreRegistry;
    private ProjectSet projectSet;
    private EclipseWorkspace workspace;
    private WorkspaceStatus updatedStatus;
    private int updates;

    @Before
    public void inContext() throws CoreException
    {
        projects = newArrayList();
        projectSet = createMock(ProjectSet.class);
        projects.add(newFacade(project));
        coreRegistry = createMock(CoreRegistry.class);
        CoreFactory coreFactory = new CoreFactory(new ControlledEventQueue());
        workspace = new EclipseWorkspace(projectSet, coreRegistry, coreFactory);

        expect(projectSet.projects()).andReturn(projects);
        expect(projectSet.hasErrors()).andReturn(false);
        List<File> outputDirs = emptyList();
        expect(projectSet.outputDirectories((EclipseProject) anyObject())).andReturn(outputDirs).anyTimes();
        replay(projectSet);
    }

    private ProjectFacade newFacade(IJavaProject project)
    {
        return new ProjectFacade(project)
        {
            @Override
            public String rawClasspath() throws CoreException
            {
                return "classpath";
            }
        };
    }

    @Test
    public void shouldCreateACoreOnUpdateIfNoneExists() throws CoreException
    {
        expect(coreRegistry.getCore(projectAUri())).andReturn(null);
        coreRegistry.addCore(eq(projectAUri()), (InfinitestCore) anyObject());
        replay(coreRegistry);

        workspace.updateProjects();

        assertStatusIs(noTestsRun());
    }

    @Test
    public void shouldFireAnEvent() throws CoreException
    {
        expectCoreUpdateForProject(projectAUri());
        replay(coreRegistry);

        workspace.addUpdateListeners(new UpdateListener()
        {
            public void projectsUpdated()
            {
                updates++;
            }
        });

        workspace.updateProjects();
        assertEquals(1, updates);
    }

    @Test
    public void shouldUpdateCoreOnAutoBuild() throws CoreException
    {
        InfinitestCore core = expectCoreUpdateForProject(projectAUri());
        replay(coreRegistry);

        workspace.updateProjects();

        verify(coreRegistry, core);
        assertStatusIs(findingTests(0));
    }

    @Test
    public void shouldSetAppropriateStatusIfNoTestsWereRun() throws CoreException
    {
        InfinitestCore core = expectCoreUpdateForProject(projectAUri(), 0);
        replay(coreRegistry);

        workspace.updateProjects();

        assertStatusIs(noTestsRun());
        verify(coreRegistry, core);
    }

    @Test
    public void shouldSetWarningStatusIfNoTestsAreRun() throws CoreException
    {
        projects.clear();
        replay(coreRegistry);

        workspace.updateProjects();

        assertStatusIs(noTestsRun());
    }

    @Test
    public void shouldFireEventIfStatusChanges() throws CoreException
    {
        projects.clear();
        replay(coreRegistry);
        workspace.addStatusListeners(new WorkspaceStatusListener()
        {
            public void statusChanged(WorkspaceStatus newStatus)
            {
                updatedStatus = newStatus;
            }
        });

        workspace.updateProjects();
        assertThat(updatedStatus, equalsStatus(noTestsRun()));
    }

    @Test
    public void shouldUpdateAllCoresWhenOneChanges() throws CoreException
    {
        String projectBName = "/projectB";
        JavaProjectBuilder projectB = project(projectBName);
        URI projectBUri = projectB.getProject().getLocationURI();
        projects.add(newFacade(projectB));

        InfinitestCore coreA = expectCoreUpdateForProject(projectAUri());
        InfinitestCore coreB = expectCoreUpdateForProject(projectBUri);
        replay(coreRegistry);

        workspace.updateProjects();
        verify(coreRegistry, coreA, coreB);
    }

    private InfinitestCore expectCoreUpdateForProject(URI projectAUri)
    {
        return expectCoreUpdateForProject(projectAUri, 10);
    }

    private InfinitestCore expectCoreUpdateForProject(URI projectAUri, int numberOfTestsRun)
    {
        InfinitestCore core = createMock(InfinitestCore.class);
        expect(core.update()).andReturn(numberOfTestsRun);
        core.setRuntimeEnvironment((RuntimeEnvironment) anyObject());
        expect(coreRegistry.getCore(projectAUri)).andReturn(core);
        replay(core);
        return core;
    }

    private void assertStatusIs(WorkspaceStatus expectedStatus)
    {
        assertThat(workspace.getStatus(), equalsStatus(expectedStatus));
        verify(coreRegistry);
    }
}
