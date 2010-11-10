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
