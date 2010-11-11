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
package org.infinitest.intellij.idea.language;

import static java.util.Collections.*;

import java.util.HashSet;
import java.util.Set;

import org.infinitest.intellij.InfinitestAnnotator;
import org.infinitest.testrunner.TestEvent;

public class IdeaInfinitestAnnotator implements InfinitestAnnotator
{
    private final Set<InnerClassFriendlyTestEvent> events = new HashSet<InnerClassFriendlyTestEvent>();

    private static IdeaInfinitestAnnotator instance;

    private IdeaInfinitestAnnotator()
    {

    }

    public static IdeaInfinitestAnnotator getInstance()
    {
        if (instance == null)
        {
            instance = new IdeaInfinitestAnnotator();
        }
        return instance;
    }

    public void annotate(final TestEvent event)
    {
        events.add(new InnerClassFriendlyTestEvent(event));
    }

    public void clearAnnotation(TestEvent event)
    {
        events.remove(new InnerClassFriendlyTestEvent(event));
    }

    public Set<InnerClassFriendlyTestEvent> getTestEvents()
    {
        return unmodifiableSet(events);
    }
}
