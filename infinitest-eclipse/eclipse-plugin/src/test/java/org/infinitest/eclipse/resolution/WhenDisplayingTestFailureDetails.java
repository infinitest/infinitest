package org.infinitest.eclipse.resolution;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.infinitest.eclipse.util.PickleJar.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.junit.Before;
import org.junit.Test;

public class WhenDisplayingTestFailureDetails
{
    protected Collection<StackTraceElement> actualStackTrace;
    private ErrorViewerResolution resolution;
    protected String actualMessage;

    @Before
    public void inContext()
    {
        actualStackTrace = newArrayList();
        resolution = new ErrorViewerResolution("TestName.methodName")
        {
            @Override
            protected void createStackViewWith(List<StackTraceElement> trace, String message)
            {
                actualStackTrace = trace;
                actualMessage = message;
            }
        };
    }

    @Test
    public void shouldUseTestAndMethodNameInLabel()
    {
        assertEquals("TestName.methodName failing (see details)", resolution.getLabel());
    }

    @Test
    public void printStackTraceWithSourceFileLinksUsingInternalJavaStackTraceConsole() throws Exception
    {
        IMarker marker = createMock(IMarker.class);
        StackTraceElement element = new StackTraceElement("", "", "", 0);
        Object pickledStackTrace = pickle(new StackTraceElement[] { element });
        expect(marker.getAttribute(PICKLED_STACK_TRACE_ATTRIBUTE)).andReturn(pickledStackTrace);
        expect(marker.getAttribute(MESSAGE)).andReturn("message");
        replay(marker);

        resolution.run(marker);
        assertEquals(element, getOnlyElement(actualStackTrace));
        assertEquals("message", actualMessage);

        verify(marker);
    }
}
