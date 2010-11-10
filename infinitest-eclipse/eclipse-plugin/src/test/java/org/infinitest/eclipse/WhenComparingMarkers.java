package org.infinitest.eclipse;

import static org.hamcrest.CoreMatchers.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import org.infinitest.eclipse.markers.ProblemMarkerInfo;
import org.infinitest.eclipse.workspace.FakeResourceFinder;
import org.junit.Before;
import org.junit.Test;

public class WhenComparingMarkers
{
    private Throwable error;
    private ProblemMarkerInfo methodError;
    private ProblemMarkerInfo methodFailure;
    private FakeResourceFinder finder;
    private AssertionError failure;

    @Before
    public void inContext()
    {
        error = new Throwable();
        failure = new AssertionError();
        failure.fillInStackTrace();
        finder = new FakeResourceFinder();
        methodError = new ProblemMarkerInfo(methodFailed("testClass", "", error), finder);
        methodFailure = new ProblemMarkerInfo(methodFailed("message", "testClass", "methodName", failure), finder);
    }

    @Test
    public void shouldBeEqualIfTestNameAndMethodNameAreEqual()
    {
        assertEquals(methodError, new ProblemMarkerInfo(methodFailed("testClass", "", error), finder));
        assertThat(methodError, not(equalTo(new ProblemMarkerInfo(methodFailed("testClass2", "", error), finder))));

        ProblemMarkerInfo errorMarker = new ProblemMarkerInfo(methodFailed("testClass", "", new AssertionError()),
                        finder);
        assertThat(methodError, equalTo(errorMarker));
        assertThat(methodFailure, not(equalTo(methodError)));
    }
}
