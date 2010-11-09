package org.infinitest.eclipse.markers;

import static com.google.common.collect.Iterables.*;
import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.eclipse.PluginConstants.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class WhenCreatingMarkers
{
    private GenericMarkerRegistry registry;
    private AssertionError error;

    @Before
    public void inContext()
    {
        error = new AssertionError();
        registry = new GenericMarkerRegistry(PROBLEM_MARKER_ID);
    }

    @Test
    public void shouldNeverAllowTwoMarkersForTheSameTest()
    {
        // This check is defensive. Although you'd think the problem marker for a test
        // would be removed before we try to add another one, we really want to make
        // sure that we don't have any lingering markers

        addMarker(methodFailed("message", "testName", "methodName", new Throwable("Fail")));
        addMarker(methodFailed("anotherMessage", "testName", "methodName", new Throwable("SuperFail")));

        MarkerInfo marker = getOnlyElement(registry.getMarkers());
        assertThat(getAttribute(marker, MESSAGE).toString(), containsString("anotherMessage"));
        assertEquals(SEVERITY_ERROR, getAttribute(marker, SEVERITY));
    }

    @Test
    public void shouldCreateNewMarkersForNewPointsOfFailure()
    {
        addMarker(failureEvent("my failure message"));

        MarkerInfo marker = getOnlyElement(registry.getMarkers());
        assertEquals("AssertionError (my failure message) in TestName.methodName", getAttribute(marker, MESSAGE));
        assertEquals(SEVERITY_ERROR, getAttribute(marker, SEVERITY));
    }

    @Test
    public void shouldGenerateMessageForFailuresWithStringifiedNullMessage()
    {
        addMarker(failureEvent("null"));

        MarkerInfo marker = getOnlyElement(registry.getMarkers());
        assertEquals(error.getClass().getSimpleName() + " in " + "TestName.methodName", getAttribute(marker, MESSAGE));
    }

    @Test
    public void shouldGenerateMessageForFailuresWithBlankMessage()
    {
        addMarker(failureEvent(""));

        MarkerInfo marker = getOnlyElement(registry.getMarkers());
        assertEquals(error.getClass().getSimpleName() + " in " + "TestName.methodName", getAttribute(marker, MESSAGE));
    }

    private Object getAttribute(MarkerInfo marker, String message)
    {
        return marker.attributes().get(message);
    }

    @Test
    public void shouldIgnoredRemovedMarkersThatDontExist()
    {
        registry.removeMarker(createMock(MarkerInfo.class));
    }

    @Test
    public void shouldRemoveMarkersWhenTestPasses()
    {
        TestEvent event = failureEvent("my failure message");
        addMarker(event);
        registry.removeMarker(new FakeProblemMarkerInfo(event));

        assertTrue(registry.getMarkers().isEmpty());
    }

    @Test
    public void shouldOnlyAddOneMarkerIfSameEventIsAddedTwice()
    {
        addMarker(failureEvent("my failure message"));
        addMarker(failureEvent("my failure message"));

        assertEquals(1, registry.getMarkers().size());
        MarkerInfo marker = getOnlyElement(registry.getMarkers());
        assertThat(getAttribute(marker, MESSAGE).toString(), containsString("my failure message"));
    }

    @Test
    public void canRemoveAllMarkersForASpecifiedTestCase()
    {
        addMarker(failureEvent("my failure message"));
        registry.removeMarkers("NotATest");
        assertFalse(registry.getMarkers().isEmpty());

        registry.removeMarkers("com.fake.TestName");
        assertTrue(registry.getMarkers().isEmpty());
    }

    @Test
    public void canClearAllMarkers()
    {
        addMarker(failureEvent("my failure message"));
        addMarker(failureEvent("another failure message"));
        addMarker(failureEvent("yet failure message"));
        registry.clear();

        assertTrue(registry.getMarkers().isEmpty());
    }

    private void addMarker(TestEvent event)
    {
        registry.addMarker(new FakeProblemMarkerInfo(event));
    }

    private TestEvent failureEvent(String failureMessage)
    {
        return methodFailed(failureMessage, "com.fake.TestName", "methodName", error);
    }
}
