package org.infinitest.eclipse.markers;

import static com.google.common.collect.Maps.*;
import static org.apache.commons.lang.StringUtils.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.infinitest.eclipse.util.PickleJar.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.PointOfFailure;
import org.infinitest.testrunner.TestEvent;

public class ProblemMarkerInfo extends AbstractMarkerInfo
{
    public static final String PICKLED_STACK_TRACE_ATTRIBUTE = "Pickled Stack Trace";
    public static final String TEST_NAME_ATTRIBUTE = "Test Name";
    public static final String METHOD_NAME_ATTRIBUTE = "Test Method";

    private final TestEvent event;
    private final MarkerPlacer placer;
    private MarkerPlacement placement;

    public ProblemMarkerInfo(TestEvent event, ResourceLookup resourceLookup)
    {
        this.event = event;
        placer = new MarkerPlacer(resourceLookup);
    }

    public String getPointOfFailureClassName()
    {
        return event.getPointOfFailure().getClassName();
    }

    /**
     * Returns the <code>IResource</code> associated with the failing <code>TestEvent</code>.
     * <p>
     * If the point of failure occurred in a class for which the source is available, the resource
     * for this class will be returned. Otherwise, will return the resource for which there is
     * source that is closest to the top of the stack.
     * </p>
     * 
     * @throws CoreException
     * @Deprecated Use getPlacement() or {@link #createMarker(String)}
     */
    @Override
    public IResource associatedResource() throws CoreException
    {
        return getPlacement().getResource();
    }

    private MarkerPlacement getPlacement()
    {
        if (placement == null)
        {
            // This is very expensive. Doing it lazily prevents us from searching for resources
            // when we don't have to (for example, when the marker already exists)
            placement = placer.findPlacement(event);
        }
        return placement;
    }

    public Map<String, Object> attributes()
    {
        Map<String, Object> markerAttributes = newLinkedHashMap();
        markerAttributes.put(SEVERITY, SEVERITY_ERROR);
        markerAttributes.put(MESSAGE, buildMessage(event));
        markerAttributes.put(PICKLED_STACK_TRACE_ATTRIBUTE, getPickledStackTrace());
        markerAttributes.put(TEST_NAME_ATTRIBUTE, event.getTestName());
        markerAttributes.put(METHOD_NAME_ATTRIBUTE, event.getTestMethod());
        markerAttributes.put(LINE_NUMBER, getPlacement().getLineNumber());

        return markerAttributes;
    }

    private String getPickledStackTrace()
    {
        // Wrapping this in a list seems to create serialization problems on Windows. Curious.
        return pickle(event.getStackTrace());
    }

    private String buildMessage(TestEvent anEvent)
    {
        PointOfFailure failure = anEvent.getPointOfFailure();
        return anEvent.getErrorClassName() + getMessage(failure) + " in " + stripPackageName(event.getTestName()) + "."
                        + anEvent.getTestMethod();
    }

    private String getMessage(PointOfFailure failure)
    {
        String message = failure.getMessage();
        if (isBlank(message) || isStringifiedNull(message))
        {
            return "";
        }
        return " (" + message + ")";
    }

    private boolean isStringifiedNull(String message)
    {
        return "null".equals(message);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MarkerInfo)
        {
            ProblemMarkerInfo other = (ProblemMarkerInfo) obj;
            return event.equals(other.event);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(event.getTestMethod()) ^ ObjectUtils.hashCode(event.getTestName());
    }

    @Override
    public String toString()
    {
        return event.toString();
    }

    public String getTestName()
    {
        return event.getTestName();
    }
}
