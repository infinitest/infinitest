package org.infinitest.testrunner.process;

import static java.util.Arrays.*;
import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.infinitest.RuntimeEnvironment;
import org.infinitest.ConsoleOutputListener.OutputType;
import org.infinitest.testrunner.NativeRunner;
import org.infinitest.testrunner.OutputStreamHandler;
import org.infinitest.testrunner.TestRunnerProcess;

public class NativeConnectionFactory implements ProcessConnectionFactory
{
    private final Class<? extends NativeRunner> runnerClass;

    public NativeConnectionFactory(Class<? extends NativeRunner> testRunnerClass)
    {
        runnerClass = testRunnerClass;
    }

    public ProcessConnection getConnection(RuntimeEnvironment environment, OutputStreamHandler outputListener)
                    throws IOException
    {
        TcpSocketProcessCommunicator communicator = createCommunicator();
        Process process = startProcess(communicator.createSocket(), environment);
        outputListener.processStream(process.getErrorStream(), OutputType.STDERR);
        outputListener.processStream(process.getInputStream(), OutputType.STDOUT);
        communicator.openSocket();
        return new NativeProcessConnection(communicator, process);
    }

    protected TcpSocketProcessCommunicator createCommunicator()
    {
        return new TcpSocketProcessCommunicator();
    }

    Process startProcess(int port, RuntimeEnvironment environment) throws IOException
    {
        ProcessBuilder builder = buildProcess(port, environment);
        return builder.start();
    }

    ProcessBuilder buildProcess(int port, RuntimeEnvironment environment)
    {
        // Could extract this to a class. Could then replace with:
        // http://wiki.eclipse.org/FAQ_How_do_I_launch_a_Java_program%3F
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(environment.getWorkingDirectory());
        List<String> arguments = environment.createProcessArguments();

        arguments.addAll(buildRunnerArgs(port));

        builder.command(arguments);
        logProcessEnvironment(builder);
        return builder;
    }

    private void logProcessEnvironment(ProcessBuilder builder)
    {
        String lineSeparator = System.getProperty("line.separator");

        StringBuilder message = new StringBuilder();
        message.append("Launching test runner process with the following configuration:").append(lineSeparator);
        message.append("Directory: ").append(builder.directory().getAbsolutePath()).append(lineSeparator).append(
                        "Environment: ").append(builder.environment()).append(lineSeparator).append("Command: ")
                        .append(builder.command());

        log(INFO, message.toString());
    }

    private Collection<String> buildRunnerArgs(int portNum)
    {
        return asList(TestRunnerProcess.class.getName(), runnerClass.getName(), String.valueOf(portNum));
    }
}
