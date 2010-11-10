package org.infinitest.testrunner;

import static com.google.common.collect.Iterables.*;
import static java.util.Arrays.*;
import static java.util.logging.Level.*;
import static org.apache.commons.io.FileUtils.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.testrunner.TestRunnerMother.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.infinitest.EventSupport;
import org.infinitest.JavaHomeException;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.util.FakeEnvironments;
import org.infinitest.util.InfinitestTestUtils;
import org.infinitest.util.LoggingAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenRunningTestsInDifferentEnvironments extends AbstractRunnerTest
{
    private EventSupport eventAssert;
    private AbstractTestRunner runner;
    public boolean outputPrinted;
    protected boolean runComplete;
    private File fakeJavaHome;

    @Before
    public void inContext()
    {
        eventAssert = new EventSupport();
        runner = createRunner();
        runner.addTestResultsListener(eventAssert);
        runner.addTestQueueListener(eventAssert);
        outputPrinted = false;
        fakeJavaHome = new File("fakeJavaHome");
        fakeJavaHome.mkdirs();
    }

    @After
    public void cleanup() throws IOException
    {
        deleteDirectory(fakeJavaHome);
    }

    @Override
    protected AbstractTestRunner getRunner()
    {
        return runner;
    }

    @Test
    public void shouldThrowExceptionOnInvalidJavaHome()
    {
        RuntimeEnvironment environment = new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(),
                        FakeEnvironments.systemClasspath(), fakeJavaHome);
        try
        {
            environment.createProcessArguments();
            fail("Should have thrown exception");
        }
        catch (JavaHomeException e)
        {
            assertThat(convertFromWindowsClassPath(e.getMessage()), 
                    containsString(convertFromWindowsClassPath(fakeJavaHome.getAbsolutePath()) + "/bin/java"));
        }
    }

    @Test
    public void shouldAllowAlternateJavaHomesOnUnixAndWindows() throws Exception
    {
        RuntimeEnvironment environment = new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(),
                        FakeEnvironments.systemClasspath(), fakeJavaHome);

        touch(new File(fakeJavaHome, "bin/java.exe"));
        List<String> arguments = environment.createProcessArguments();
        assertEquals(convertFromWindowsClassPath(fakeJavaHome.getAbsolutePath()) + "/bin/java.exe",
                convertFromWindowsClassPath(get(arguments, 0)));

        touch(new File(fakeJavaHome, "bin/java"));
        arguments = environment.createProcessArguments();
        assertEquals(convertFromWindowsClassPath(fakeJavaHome.getAbsolutePath()) + "/bin/java",
                convertFromWindowsClassPath(get(arguments, 0)));
    }

    @Test
    public void shouldAddInfinitestJarOrClassDirToClasspath()
    {
        RuntimeEnvironment environment = new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(),
                        systemClasspath(), currentJavaHome());
        String classpath = environment.getCompleteClasspath();
        assertTrue(classpath, classpath.contains("infinitest"));
    }

    @Test
    public void shouldLogErrorIfInfinitestJarCannotBeFound()
    {
        LoggingAdapter listener = new LoggingAdapter();
        addLoggingListener(listener);

        RuntimeEnvironment environment = emptyRuntimeEnvironment();
        environment.setInfinitestRuntimeClassPath("noClasses");
        environment.getCompleteClasspath();
        assertTrue(listener.hasMessage("Could not find a classpath entry for Infinitest Core in noClasses", SEVERE));
    }

    @Test
    public void canSetAdditionalVMArguments()
    {
        RuntimeEnvironment environment = fakeEnvironment();
        List<String> additionalArgs = asList("-Xdebug", "-Xnoagent",
                        "-Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=y");
        environment.addVMArgs(additionalArgs);
        List<String> actualArgs = environment.createProcessArguments();
        assertTrue(actualArgs.toString(), actualArgs.containsAll(additionalArgs));
    }

    @Test
    public void canUseACustomWorkingDirectory() throws Exception
    {
        runner.setRuntimeEnvironment(new RuntimeEnvironment(fakeBuildPaths(), new File("src"), FakeEnvironments
                        .systemClasspath(), currentJavaHome()));
        runTests(WorkingDirectoryVerifier.class);
        eventAssert.assertTestPassed(WorkingDirectoryVerifier.class);
    }

    public static class WorkingDirectoryVerifier
    {
        @Test
        public void shouldFailIfRunningInADifferentWorkingDirectory()
        {
            assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
            assertTrue(new File("").getAbsoluteFile().getAbsolutePath().endsWith("src"));
        }
    }

    @Override
    protected void waitForCompletion() throws InterruptedException
    {
        eventAssert.assertRunComplete();
    }
}
