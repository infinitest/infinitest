package org.infinitest.eclipse;

import static java.util.Arrays.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.infinitest.eclipse.markers.MarkerInfo;
import org.infinitest.eclipse.markers.SlowMarkerRegistry;
import org.infinitest.eclipse.markers.SlowTestMarkerInfo;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.MethodStats;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenWatchingForSlowTests
{
    private SlowMarkerRegistry mockMarkerRegistry;
    private MethodStats methodStats;
    private MarkerInfo expectedMarker;
    private SlowTestObserver observer;
    private TestCaseEvent event;

    @Before
    public void inContext()
    {
        mockMarkerRegistry = mock(SlowMarkerRegistry.class);
        methodStats = new MethodStats("shouldRunSlowly");
        ResourceLookup resourceLookup = mock(ResourceLookup.class);
        expectedMarker = new SlowTestMarkerInfo("MyTest", methodStats, resourceLookup);
        setSlowTestTimeLimit(2000);
        observer = new SlowTestObserver(mockMarkerRegistry, resourceLookup);

        TestResults results = new TestResults();
        results.addMethodStats(asList(methodStats));
        event = new TestCaseEvent("MyTest", this, results);
    }

    @After
    public void cleanup()
    {
        resetToDefaults();
    }

    @Test
    public void shouldAddMarkersForSlowTests()
    {
        methodStats.startTime = 1000;
        methodStats.stopTime = 5000;

        observer.testCaseComplete(event);

        verify(mockMarkerRegistry).addMarker(expectedMarker);
    }

    @Test
    public void shouldRemoveMarkersForFastTests()
    {
        methodStats.startTime = 1000;
        methodStats.stopTime = 1001;

        observer.testCaseComplete(event);

        verify(mockMarkerRegistry).removeMarker(expectedMarker);
    }

    @Test
    public void shouldRemoveMarkersWhenTestsAreDisabled()
    {
        methodStats.startTime = 1000;
        methodStats.stopTime = 5000;

        observer.testCaseComplete(event);
        observer.testsDisabled(Arrays.asList("MyTest"));

        verify(mockMarkerRegistry).addMarker(expectedMarker);
        verify(mockMarkerRegistry).removeMarkers("MyTest");
    }

    @Test
    public void canRemoveAllMarkers()
    {
        observer.clearMarkers();

        verify(mockMarkerRegistry).clear();
    }
}
