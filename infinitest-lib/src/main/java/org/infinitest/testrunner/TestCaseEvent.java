package org.infinitest.testrunner;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static java.util.Collections.*;

import java.util.List;
import java.util.Set;

public class TestCaseEvent
{
    private static final Set<String> IGNORED_ERRORS = newHashSet(VerifyError.class.getName(), Error.class.getName());
    private final List<TestEvent> methodEvents;
    private final Object source;
    private final String testName;
    private final TestResults results;

    public TestCaseEvent(String testName, Object source, TestResults results)
    {
        this.testName = testName;
        this.source = source;
        this.results = results;
        this.methodEvents = newArrayList();
        // DEBT move this to a static factory method?
        for (TestEvent testEvent : results)
        {
            if (!IGNORED_ERRORS.contains(testEvent.getFullErrorClassName()))
            {
                this.methodEvents.add(testEvent);
            }
        }
    }

    public boolean failed()
    {
        return !getFailureEvents().isEmpty();
    }

    public Object getSource()
    {
        return source;
    }

    public String getTestName()
    {
        return testName;
    }

    public List<TestEvent> getFailureEvents()
    {
        return unmodifiableList(methodEvents);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof TestCaseEvent)
        {
            TestCaseEvent other = (TestCaseEvent) obj;
            return testName.equals(other.testName);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return testName.hashCode();
    }

    public Iterable<MethodStats> getRunStats()
    {
        return results.getMethodStats();
    }
}
