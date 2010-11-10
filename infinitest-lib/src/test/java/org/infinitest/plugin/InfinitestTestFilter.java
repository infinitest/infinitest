package org.infinitest.plugin;

import org.infinitest.filter.TestFilter;

class InfinitestTestFilter implements TestFilter
{
    public boolean match(String className)
    {
        return !className.startsWith("com.fakeco.fakeproduct.simple");
    }

    public void updateFilterList()
    {
        // nothing to do here
    }
}
