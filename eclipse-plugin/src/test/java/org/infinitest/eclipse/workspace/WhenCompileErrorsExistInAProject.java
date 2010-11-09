package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Lists.newArrayList;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.infinitest.eclipse.util.StatusMatchers.equalsStatus;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.workspaceErrors;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.infinitest.ControlledEventQueue;
import org.infinitest.eclipse.ResourceEventSupport;
import org.infinitest.eclipse.status.WorkspaceStatus;
import org.junit.Before;
import org.junit.Test;

// DEBT Duplication with WhenUpdatingTheProjectsInTheWorkspace
public class WhenCompileErrorsExistInAProject extends ResourceEventSupport
{
    private List<ProjectFacade> projects;
    private CoreRegistry coreRegistry;
    private ProjectSet projectSet;
    private EclipseWorkspace workspace;

    @Before
    public void inContext() throws CoreException
    {
        projects = newArrayList();
        projectSet = createMock(ProjectSet.class);
        projects.add(new ProjectFacade(project));
        coreRegistry = createMock(CoreRegistry.class);
        CoreFactory coreFactory = new CoreFactory(new ControlledEventQueue());
        workspace = new EclipseWorkspace(projectSet, coreRegistry, coreFactory);

        expect(projectSet.projects()).andReturn(projects);
        expect(projectSet.hasErrors()).andReturn(true);
        replay(projectSet);
    }

    @Test
    public void shouldNotUpdateIt() throws CoreException
    {
        IJavaProject project = createMock(IJavaProject.class);
        IProject projectWithErrors = createMock(IProject.class);
        projects.clear();
        projects.add(new ProjectFacade(project));
        replay(coreRegistry, project, projectWithErrors);

        workspace.updateProjects();

        assertStatusIs(workspaceErrors());
        verify(project, projectWithErrors);
    }

    private void assertStatusIs(WorkspaceStatus expectedStatus)
    {
        assertThat(workspace.getStatus(), equalsStatus(expectedStatus));
        verify(coreRegistry);
    }
}
