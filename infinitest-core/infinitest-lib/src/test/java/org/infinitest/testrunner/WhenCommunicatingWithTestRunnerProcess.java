package org.infinitest.testrunner;

import static com.google.common.collect.Iterables.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.infinitest.testrunner.process.TcpSocketProcessCommunicator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenCommunicatingWithTestRunnerProcess
{
    private TcpSocketProcessCommunicator communicator;

    @Before
    public void inContext()
    {
        communicator = new TcpSocketProcessCommunicator();
    }

    @After
    public void cleanup()
    {
        communicator.closeSocket();
    }

    @Test
    public void shouldOpenAPort()
    {
        assertFalse("Port cannot be zero", communicator.createSocket() == 0);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfTwoSocketsAreCreated()
    {
        communicator.createSocket();
        communicator.createSocket();
    }

    @Test
    public void shouldReadResults()
    {
        final int portNum = communicator.createSocket();
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    Socket clientSocket = new Socket("127.0.0.1", portNum);
                    ObjectOutputStream ooStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
                    assertEquals("hello", inStream.readObject());
                    ooStream.writeObject(new TestResults(testCaseStarting("hello")));
                    assertNull(inStream.readObject());
                    ooStream.writeObject(new TestResults());
                    clientSocket.close();
                }
                catch (UnknownHostException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        communicator.openSocket();
        assertEquals(1, size(communicator.sendMessage("hello")));
        assertTrue(isEmpty(communicator.sendMessage(null)));
    }

    @Test(timeout = 1000)
    public void shouldTimeOutIfRunnerProcessFailsToStart()
    {
        communicator = new TcpSocketProcessCommunicator(250);
        communicator.createSocket();
        try
        {
            communicator.openSocket();
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getCause().toString(), e.getCause() instanceof SocketTimeoutException);
        }
    }
}
