/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
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
