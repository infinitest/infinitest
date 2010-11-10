package org.infinitest.testrunner;

import static com.google.common.collect.Lists.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import java.util.List;

import org.infinitest.toolkit.EqualsHashCodeTestSupport;
import org.junit.Before;
import org.junit.Test;

public class TestCaseEventTest extends EqualsHashCodeTestSupport
{
    private TestCaseEvent event;
    private List<TestEvent> methodEvents;
    private Object source;

    @Before
    public void inContext()
    {
        methodEvents = newArrayList();
        source = "";
        event = new TestCaseEvent("testName", source, new TestResults(methodEvents));
    }

    @Test
    public void shouldIgnoreCompilerErrors()
    {
        methodEvents.add(methodFailed("TestClass", "", new VerifyError()));
        methodEvents.add(methodFailed("TestClass", "", new Error()));
        event = new TestCaseEvent("testName", source, new TestResults(methodEvents));
        assertFalse(event.failed());
    }

    @Test
    public void shouldContainMethodEvents()
    {
        assertFalse(event.failed());
    }

    @Test
    public void shouldIndicateSourceOfEvent()
    {
        assertEquals(source, event.getSource());
    }

    @Override
    protected TestCaseEvent equal() throws Exception
    {
        return new TestCaseEvent("testName", source, new TestResults());
    }

    @Override
    protected Object notEqual() throws Exception
    {
        return new TestCaseEvent("testName2", source, new TestResults());
    }
}
