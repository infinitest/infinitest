package org.infinitest.intellij.idea.language;

import org.infinitest.testrunner.TestEvent;

public class InnerClassFriendlyTestEvent
{
    private TestEvent event;

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
            return className.substring(0, className.indexOf("$"));
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
