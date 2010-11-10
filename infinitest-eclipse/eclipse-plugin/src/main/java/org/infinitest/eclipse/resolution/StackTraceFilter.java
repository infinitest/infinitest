package org.infinitest.eclipse.resolution;

import static com.google.common.collect.Lists.*;

import java.util.List;

import org.infinitest.filter.ClassNameFilter;

public class StackTraceFilter
{
    private final ClassNameFilter nameFilter;

    public StackTraceFilter()
    {
        nameFilter = new ClassNameFilter();
        nameFilter.addFilter("org\\.infinitest\\.runner\\.*");
        nameFilter.addFilter("org\\.junit\\..*");
        nameFilter.addFilter("junit\\.framework\\..*");
        nameFilter.addFilter("sun\\.reflect\\..*");
        nameFilter.addFilter("java\\.lang\\.reflect\\.Method");
    }

    public List<StackTraceElement> filterStack(List<StackTraceElement> stackTrace)
    {
        List<StackTraceElement> filteredStackTrace = newArrayList();
        for (StackTraceElement each : stackTrace)
        {
            if (!nameFilter.match(each.getClassName()))
            {
                filteredStackTrace.add(each);
            }
        }
        return filteredStackTrace;
    }
}
