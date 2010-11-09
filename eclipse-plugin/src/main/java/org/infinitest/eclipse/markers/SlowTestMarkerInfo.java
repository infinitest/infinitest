package org.infinitest.eclipse.markers;

import static com.google.common.collect.Maps.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.MethodStats;

public class SlowTestMarkerInfo extends AbstractMarkerInfo
{
    private final String testName;
    private final MethodStats methodStats;
    private final ResourceLookup lookup;

    public SlowTestMarkerInfo(String testName, MethodStats methodStats, ResourceLookup lookup)
    {
        this.testName = testName;
        this.methodStats = methodStats;
        this.lookup = lookup;
    }

    public IResource associatedResource() throws CoreException
    {
        List<IResource> resources = lookup.findResourcesForClassName(testName);
        if (resources.isEmpty())
            return null;
        return resources.get(0);
    }

    public Map<String, Object> attributes()
    {
        Map<String, Object> markerAttributes = newLinkedHashMap();
        markerAttributes.put(SEVERITY, SEVERITY_WARNING);
        markerAttributes.put(MESSAGE, buildMessage());

        return markerAttributes;
    }

    private String buildMessage()
    {
        return stripPackageName(testName) + "." + methodStats.methodName + " ran in " + methodStats.duration() + "ms";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof SlowTestMarkerInfo)
        {
            SlowTestMarkerInfo other = (SlowTestMarkerInfo) obj;
            return testName.equals(other.testName) && methodStats.methodName.equals(other.methodStats.methodName);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return testName.hashCode() ^ methodStats.methodName.hashCode();
    }

    public String getTestName()
    {
        return testName;
    }

}
