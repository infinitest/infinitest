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

import org.infinitest.testrunner.TestEvent;

public class InnerClassFriendlyTestEvent
{
    private final TestEvent event;

    public InnerClassFriendlyTestEvent(TestEvent event)
    {
        this.event = event;
    }

    @Override
    public int hashCode()
    {
        return event.hashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof InnerClassFriendlyTestEvent))
        {
            return false;
        }
        InnerClassFriendlyTestEvent otherEvent = (InnerClassFriendlyTestEvent) other;
        return event.equals(otherEvent.event);
    }

    public String getPointOfFailureClassName()
    {
        String className = event.getPointOfFailure().getClassName();
        if (className.contains("$"))
        {
            return className.substring(0, className.indexOf("$"));
        }
        return className;
    }

    public int getPointOfFailureLineNumber()
    {
        return event.getPointOfFailure().getLineNumber();
    }

    public String getMessage()
    {
        return event.getMessage();
    }

    public String getErrorClassName()
    {
        return event.getErrorClassName();
    }

    public String getTestName()
    {
        return event.getTestName();
    }

    public String getTestMethod()
    {
        return event.getTestMethod();
    }
}
