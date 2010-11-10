package org.infinitest.eclipse;

import static java.util.Arrays.*;
import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.junit.Assert.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.markers.SlowTestMarkerInfo;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.MethodStats;
import org.infinitest.toolkit.EqualsHashCodeTestSupport;
import org.junit.Before;
import org.junit.Test;

public class WhenMarkingTestsAsSlow extends EqualsHashCodeTestSupport
{
    private static final String TEST_NAME = "com.foo.TestName";
    private SlowTestMarkerInfo marker;
    private ResourceLookup resourceLookup;

    @Before
    public void inContext()
    {
        resourceLookup = createMock(ResourceLookup.class);
        MethodStats stats = new MethodStats("shouldRunSlowly");
        stats.stopTime = 5000;
        marker = new SlowTestMarkerInfo(TEST_NAME, stats, resourceLookup);
    }

    @Test
    public void shouldPlaceMarkerInSlowTest() throws CoreException
    {
        IResource resource = createMock(IResource.class);
        expect(resourceLookup.findResourcesForClassName(TEST_NAME)).andReturn(asList(resource));
        replay(resourceLookup);
        assertSame(resource, marker.associatedResource());
    }

    @Test
    public void shouldUseWarningSeverity()
    {
        assertEquals(SEVERITY_WARNING, marker.attributes().get(SEVERITY));
    }

    @Test
    public void shouldIndicateTestAndMethodName()
    {
        assertEquals("TestName.shouldRunSlowly ran in 5000ms", marker.attributes().get(MESSAGE));
    }

    @Override
    protected Object equal() throws Exception
    {
        return new SlowTestMarkerInfo(TEST_NAME, new MethodStats("shouldRunSlowly"), resourceLookup);
    }

    @Override
    protected Object notEqual() throws Exception
    {
        return new SlowTestMarkerInfo(TEST_NAME, new MethodStats("shouldRunQuickly"), resourceLookup);
    }
}
