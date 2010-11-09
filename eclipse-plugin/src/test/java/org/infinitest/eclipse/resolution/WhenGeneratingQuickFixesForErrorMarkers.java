package org.infinitest.eclipse.resolution;

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;
import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.junit.Assert.*;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IMarkerResolution;
import org.junit.Before;
import org.junit.Test;

public class WhenGeneratingQuickFixesForErrorMarkers
{
    private MarkerResolutionGenerator generator;
    private IMarker marker;

    @Before
    public void setUp()
    {
        generator = new MarkerResolutionGenerator();
        marker = createMock(IMarker.class);
    }

    @Test
    public void shouldIncludePrintStackTraceIfMarkerHasAStackTrace() throws Exception
    {
        expect(marker.getAttribute(TEST_NAME_ATTRIBUTE)).andReturn("TestName").anyTimes();
        expect(marker.getAttribute(METHOD_NAME_ATTRIBUTE)).andReturn("methodName").anyTimes();
        replay(marker);

        assertTrue(generator.hasResolutions(marker));
        IMarkerResolution resolution = generator.getResolutions(marker)[0];
        assertThat(resolution, is(ErrorViewerResolution.class));
    }

    @Test
    public void shouldNotAddResolutionsToMarkersWithNoStackTrace() throws Exception
    {
        IMarker marker = createMock(IMarker.class);
        expect(marker.getAttribute(TEST_NAME_ATTRIBUTE)).andReturn(null).anyTimes();
        replay(marker);

        assertFalse(generator.hasResolutions(marker));
        assertEquals(0, generator.getResolutions(marker).length);
    }

    @Test
    public void shouldNotHaveResolutionIfMarkerThrowsResourceException() throws CoreException
    {
        IMarker marker = createMock(IMarker.class);
        expect(marker.getAttribute(TEST_NAME_ATTRIBUTE)).andThrow(new ResourceException(createNiceMock(IStatus.class)));
        replay(marker);

        assertFalse(generator.hasResolutions(marker));
    }
}
