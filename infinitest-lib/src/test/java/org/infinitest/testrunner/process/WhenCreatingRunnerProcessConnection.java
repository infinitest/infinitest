package org.infinitest.testrunner.process;

import static com.google.common.collect.Iterables.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.infinitest.testrunner.CrashingTestRunner;
import org.infinitest.testrunner.FakeRunner;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;
import org.junit.Test;

public class WhenCreatingRunnerProcessConnection
{
    private NativeConnectionFactory factory;

    @Before
    public void setUp()
    {
        factory = new NativeConnectionFactory(FakeRunner.class);
    }

    @Test
    public void canCommunicateWithProcess() throws Exception
    {
        assertEquals("Hello World", sendMessageWithServerSocket("Hello World").get(0).getTestName());
    }

    @Test
    public void shouldPassMultipleArguments() throws Exception
    {
        List<TestEvent> events = sendMessageWithServerSocket("Hello", "World");
        assertEquals("Hello", events.get(0).getTestName());
        assertEquals(2, events.size());
        assertEquals("World", events.get(1).getTestName());
    }

    private List<TestEvent> sendMessageWithServerSocket(String input) throws UnknownHostException, IOException,
                    ClassNotFoundException
    {
        return sendMessageWithServerSocket(new String[] { input });
    }

    @SuppressWarnings("unchecked")
    private List<TestEvent> sendMessageWithServerSocket(String... messages) throws UnknownHostException, IOException,
                    ClassNotFoundException
    {
        ServerSocket serverSocket = new ServerSocket(0);
        try
        {
            factory.startProcess(serverSocket.getLocalPort(), fakeEnvironment());
            Socket socket = serverSocket.accept();
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            List<TestEvent> results = new ArrayList();
            TestResults result = null;
            int i = 0;
            do
            {
                outStream.writeObject(messages[i++]);
                result = (TestResults) inStream.readObject();
                if (result != null)
                {
                    addAll(results, result);
                }
            } while (i < messages.length);
            outStream.writeObject(null);
            inStream.close();
            outStream.close();
            socket.close();
            return results;
        }
        finally
        {
            serverSocket.close();
        }
    }

    @Test
    public void shouldGracefullyHandleErrors() throws Exception
    {
        factory = new NativeConnectionFactory(CrashingTestRunner.class);
        TestEvent testEvent = sendMessageWithServerSocket("Hello").get(0);
        assertEquals(METHOD_FAILURE, testEvent.getType());
        assertEquals("StubErrorTestRunner is Broken", testEvent.getMessage());
    }
}
