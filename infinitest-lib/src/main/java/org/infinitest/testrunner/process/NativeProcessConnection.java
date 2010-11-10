package org.infinitest.testrunner.process;

import org.infinitest.testrunner.TestResults;

public class NativeProcessConnection implements ProcessConnection
{
    private final TcpSocketProcessCommunicator communicator;
    private final Process process;

    public NativeProcessConnection(TcpSocketProcessCommunicator communicator, Process process)
    {
        this.communicator = communicator;
        this.process = process;
    }

    public boolean abort()
    {
        process.destroy();
        try
        {
            process.waitFor();
        }
        catch (InterruptedException e)
        {
            return false;
        }
        return true;
    }

    public void close()
    {
        communicator.closeSocket();
    }

    public TestResults runTest(String testName)
    {
        return communicator.sendMessage(testName);
    }
}
