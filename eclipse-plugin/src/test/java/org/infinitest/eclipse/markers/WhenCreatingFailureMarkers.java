package org.infinitest.eclipse.markers;

import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.infinitest.eclipse.util.PickleJar.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import org.infinitest.eclipse.workspace.FakeResourceFinder;
import org.junit.Test;

public class WhenCreatingFailureMarkers
{
    @Test
    public void shouldStorePickledStackTraces()
    {
        Throwable throwable = new Throwable();
        throwable.fillInStackTrace();
        ProblemMarkerInfo info = new ProblemMarkerInfo(methodFailed("testName", "methodName", throwable),
                        new FakeResourceFinder());
        
        String pickledStackTrace = info.attributes().get(PICKLED_STACK_TRACE_ATTRIBUTE).toString();
        StackTraceElement[] stackTrace = (StackTraceElement[]) unpickle(pickledStackTrace);
        assertEquals(throwable.getStackTrace().length, stackTrace.length);
    }
}
