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
