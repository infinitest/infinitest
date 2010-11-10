package org.infinitest.testrunner;

import static org.easymock.EasyMock.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.infinitest.EventSupport;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.testrunner.process.ProcessConnection;
import org.infinitest.testrunner.process.ProcessConnectionFactory;
import org.junit.Before;
import org.junit.Test;

public class ResultProcessorTest
{
    private TestQueueProcessor reader;

    private ProcessConnectionFactory factory;
    private EventSupport eventAssert;
    private RunnerEventSupport runnerEventSupport;

    private ProcessConnection connection;

    @Before
    public void inContext() throws IOException
    {
        eventAssert = new EventSupport();
        factory = createMock(ProcessConnectionFactory.class);
        runnerEventSupport = new RunnerEventSupport(this);
        runnerEventSupport.addTestQueueListener(eventAssert);
        runnerEventSupport.addTestStatusListener(eventAssert);
        connection = createMock(ProcessConnection.class);
        expect(factory.getConnection((RuntimeEnvironment) eq(null), (OutputStreamHandler) anyObject())).andReturn(
                        connection);
        replay(factory);
        reader = new TestQueueProcessor(runnerEventSupport, factory, null);
        connection.close();

        expect(connection.runTest("test1")).andReturn(new TestResults());
    }

    @Test
    public void shouldRunGivenTest() throws Exception
    {
        replay(connection);
        reader.process("test1");
        reader.close();

        eventAssert.assertTestsStarted("test1");
        eventAssert.assertTestPassed("test1");
        eventAssert.assertRunComplete();

        verify(factory, connection);
    }

    @Test
    public void shouldOnlyOpenOneConnection() throws Exception
    {
        expect(connection.runTest("test2")).andReturn(new TestResults());

        replay(connection);
        reader.process("test1");
        reader.process("test2");
        reader.close();
        eventAssert.assertRunComplete();

        verify(factory, connection);
    }

    @Test
    public void shouldFireStartingEventBeforeTestStarts() throws Exception
    {
        expect(connection.runTest("test2")).andThrow(new RuntimeException());
        replay(connection);
        try
        {
            reader.process("test2");
            fail("shouldHaveThrownException");
        }
        catch (RuntimeException expected)
        {
            // ok
        }

        reader.close();

        eventAssert.assertEventsReceived(TEST_CASE_STARTING);
        eventAssert.assertRunComplete();
        verify(factory);
    }
}
