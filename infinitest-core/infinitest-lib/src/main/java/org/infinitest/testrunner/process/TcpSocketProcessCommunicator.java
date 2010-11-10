package org.infinitest.testrunner.process;

import static org.infinitest.util.InfinitestUtils.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

import org.infinitest.MissingClassException;
import org.infinitest.TestRunAborted;
import org.infinitest.testrunner.TestResults;

public class TcpSocketProcessCommunicator
{
    private ServerSocket serverSocket;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private Socket socket;
    private final int timeout;

    public TcpSocketProcessCommunicator()
    {
        this(2500);
    }

    public TcpSocketProcessCommunicator(int timeout)
    {
        this.timeout = timeout;
    }

    public int createSocket()
    {
        if (serverSocket != null && !serverSocket.isClosed())
            throw new IllegalStateException("Test runner socket is already open");

        try
        {
            serverSocket = new ServerSocket(0);
            serverSocket.setSoTimeout(timeout);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot open port for interprocess communication", e);
        }
        return serverSocket.getLocalPort();
    }

    public void openSocket()
    {
        try
        {
            socket = serverSocket.accept();
            log(Level.CONFIG, "Socket opened");
            inStream = new ObjectInputStream(socket.getInputStream());
            outStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (SocketTimeoutException e)
        {
            log("Test runner process failed to start in a timely manner", e);
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            log("Error connecting to test runner process", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @see #sendMessage(String)
     */
    public synchronized void closeSocket()
    {
        try
        {
            if (outStream != null)
            {
                outStream.writeObject(null);
                if (!socket.isClosed())
                {
                    inStream.close();
                    inStream = null;
                    outStream.close();
                    outStream = null;
                    socket.close();
                    socket = null;
                    log(Level.CONFIG, "Socket closed");
                }
            }
        }
        catch (IOException e)
        {
            log(Level.INFO, "Tried to close socket, but was already closed");
        }
    }

    // Synchronized to prevent sending a message while the socket is being closed
    public synchronized TestResults sendMessage(String testName)
    {
        try
        {
            outStream.writeObject(testName);
            return (TestResults) inStream.readObject();
        }
        catch (IOException e)
        {
            throw new TestRunAborted(testName, e);
        }
        catch (ClassNotFoundException e)
        {
            log("Error reading from socket", e);
            throw new MissingClassException("Error reading from socket", e);
        }

    }
}
