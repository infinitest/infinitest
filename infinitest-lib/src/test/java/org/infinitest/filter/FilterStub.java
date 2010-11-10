package org.infinitest.filter;

import java.util.ArrayList;
import java.util.List;

public class FilterStub implements TestFilter
{
    private final List<String> classesToFilter = new ArrayList<String>();

    public boolean match(String className)
    {
        return classesToFilter.contains(className);
    }

    public void updateFilterList()
    {
        // nothing to do here
    }

    public void addClass(String className)
    {
        classesToFilter.add(className);
    }
}
