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
package org.infinitest.testrunner.process;

import static com.google.common.collect.Iterables.*;
import static org.infinitest.testrunner.FailingRunner.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.SynchronousQueue;

import org.apache.commons.io.IOUtils;
import org.infinitest.ConsoleOutputListener.OutputType;
import org.infinitest.testrunner.FailingRunner;
import org.infinitest.testrunner.HangingRunner;
import org.infinitest.testrunner.OutputStreamHandler;
import org.infinitest.testrunner.RunnerThatCannotBeCreated;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestRunnerProcess;
import org.junit.Test;

public class NativeConnectionFactoryTest
{
    protected String errorMsg;

    @Test
    public void shouldRunTests() throws Exception
    {
        NativeConnectionFactory manager = new NativeConnectionFactory(FailingRunner.class);
        ProcessConnection connection = manager.getConnection(fakeEnvironment(), new NoOpOutputHandler());
        Iterable<TestEvent> results = connection.runTest("testName");
        connection.close();
        assertEquals(FAILING_EVENT, getOnlyElement(results));
    }

    @Test
    public void canStopTestRun() throws Exception
    {
        final SynchronousQueue<String> testQueue = new SynchronousQueue<String>();
        NativeConnectionFactory manager = new NativeConnectionFactory(HangingRunner.class);
        final ProcessConnection connection = manager.getConnection(fakeEnvironment(), new NoOpOutputHandler());

        new Thread(new WrappedRunnable()
        {
            @Override
            protected void runWrapped() throws Exception
            {
                try
                {
                    connection.runTest(testQueue.take());
                }
                finally
                {
                    testQueue.put("finished");
                }
            }
        }).start();

        testQueue.put("test1");
        assertTrue(connection.abort());
        assertEquals("finished", testQueue.take());
    }

    @Test
    public void shouldPrintConsoleOutputEvenIfRunnerFailsToStart() throws Exception
    {
        NativeConnectionFactory manager = new NativeConnectionFactory(RunnerThatCannotBeCreated.class)
        {
            @Override
            protected TcpSocketProcessCommunicator createCommunicator()
            {
                return new TcpSocketProcessCommunicator(100);
            }
        };
        OutputStreamHandler outputListener = new OutputStreamHandler()
        {
            public void processStream(InputStream stream, OutputType type)
            {
                try
                {
                    errorMsg = IOUtils.toString(stream);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        };
        try
        {
            manager.getConnection(fakeEnvironment(), outputListener);
            fail("Failed to throw error on test run failure");
        }
        catch (RuntimeException e)
        {
            assertTrue(errorMsg.contains(TestRunnerProcess.TEST_RUN_ERROR));
        }
    }
}
