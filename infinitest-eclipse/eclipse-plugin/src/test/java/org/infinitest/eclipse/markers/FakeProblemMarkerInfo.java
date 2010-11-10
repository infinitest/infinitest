package org.infinitest.eclipse.markers;

import static org.easymock.EasyMock.*;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.workspace.FakeResourceFinder;
import org.infinitest.testrunner.TestEvent;

final class FakeProblemMarkerInfo extends ProblemMarkerInfo
{
    public FakeProblemMarkerInfo(TestEvent event)
    {
        super(event, new FakeResourceFinder());
    }

    @Override
    public IResource associatedResource() throws CoreException
    {
        IResource mockResource = createNiceMock(IResource.class);
        IMarker marker = createNiceMock(IMarker.class);
        expect(mockResource.createMarker(isA(String.class))).andReturn(marker);

        replay(mockResource);
        return mockResource;
    }
}