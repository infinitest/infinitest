package org.infinitest.filter;

import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang.StringUtils.*;

import java.util.List;
import java.util.regex.Pattern;

public class ClassNameFilter
{
    private List<Pattern> filters;

    public ClassNameFilter()
    {
        filters = newArrayList();
    }

    protected void clearFilters()
    {
        filters = newArrayList();
    }

    public boolean match(String className)
    {
        for (Pattern pattern : filters)
        {
            if (pattern.matcher(className).lookingAt())
                return true;
        }
        return false;
    }

    public void addFilter(String regex)
    {
        if (isValidFilter(regex))
            filters.add(Pattern.compile(regex));
    }

    private boolean isValidFilter(String line)
    {
        return !isBlank(line) && !line.startsWith("!") && !line.startsWith("#");
    }
}
