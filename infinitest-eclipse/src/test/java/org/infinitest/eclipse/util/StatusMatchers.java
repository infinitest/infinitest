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
package org.infinitest.eclipse.util;

import org.apache.commons.lang.ObjectUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.infinitest.eclipse.status.WorkspaceStatus;

public abstract class StatusMatchers
{
    public static Matcher<WorkspaceStatus> equalsStatus(final WorkspaceStatus expectedStatus)
    {
        return new BaseMatcher<WorkspaceStatus>()
        {
            public boolean matches(Object item)
            {
                if (ObjectUtils.equals(expectedStatus, item))
                {
                    return true;
                }
                if (item instanceof WorkspaceStatus)
                {
                    WorkspaceStatus actualStatus = (WorkspaceStatus) item;
                    return expectedStatus.getMessage().equals(actualStatus.getMessage())
                                    && expectedStatus.getToolTip().equals(actualStatus.getToolTip());
                }
                return false;
            }

            public void describeTo(Description description)
            {
                if (expectedStatus == null)
                {
                    description.appendText("null status");
                }
                else
                {
                    description.appendText("status with the message " + expectedStatus.getMessage());
                }
            }
        };
    }
}
