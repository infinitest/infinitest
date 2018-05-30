/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.testrunner;

import static com.google.common.collect.Iterables.*;
import static com.google.common.io.Files.*;
import static java.util.Arrays.*;
import static java.util.logging.Level.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.testrunner.TestRunnerMother.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.*;
import java.util.*;

import org.infinitest.*;
import org.infinitest.util.*;
import org.junit.*;
import org.junit.rules.*;

public class WhenRunningTestsInDifferentEnvironments extends AbstractRunnerTest {
  private EventSupport eventAssert;
  private AbstractTestRunner runner;
  public boolean outputPrinted;
  protected boolean runComplete;
  private File fakeJavaHome;

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Before
  public void inContext() {
    eventAssert = new EventSupport();
    runner = createRunner();
    runner.addTestResultsListener(eventAssert);
    runner.addTestQueueListener(eventAssert);
    outputPrinted = false;
    fakeJavaHome = temporaryFolder.getRoot();
    new File(fakeJavaHome, "bin").mkdirs();
  }

  @Override
  protected AbstractTestRunner getRunner() {
    return runner;
  }

  @Test
  public void shouldThrowExceptionOnInvalidJavaHome() {
    RuntimeEnvironment environment = new RuntimeEnvironment(fakeJavaHome, fakeWorkingDirectory(), "runnerClassLoaderClassPath", "runnerProcessClassPath", fakeBuildPaths(), FakeEnvironments.systemClasspath());
    try {
      environment.createProcessArguments(new File("file.classpath"));
      fail("Should have thrown exception");
    } catch (JavaHomeException e) {
      assertThat(convertFromWindowsClassPath(e.getMessage())).contains(convertFromWindowsClassPath(fakeJavaHome.getAbsolutePath()) + "/bin/java");
    }
  }

  @Test
  public void shouldAllowAlternateJavaHomesOnUnixAndWindows() throws Exception {
    RuntimeEnvironment environment = new RuntimeEnvironment(fakeJavaHome, fakeWorkingDirectory(), "runnerClassLoaderClassPath", "runnerProcessClassPath", fakeBuildPaths(), FakeEnvironments.systemClasspath());

    touch(new File(fakeJavaHome, "bin/java.exe"));
    List<String> arguments = environment.createProcessArguments(new File("file.classpath"));
    assertEquals(convertFromWindowsClassPath(fakeJavaHome.getAbsolutePath()) + "/bin/java.exe", convertFromWindowsClassPath(get(arguments, 0)));

    touch(new File(fakeJavaHome, "bin/java"));
    arguments = environment.createProcessArguments(new File("file.classpath"));
    assertEquals(convertFromWindowsClassPath(fakeJavaHome.getAbsolutePath()) + "/bin/java", convertFromWindowsClassPath(get(arguments, 0)));
  }

  @Test
  public void shouldAddInfinitestJarOrClassDirToClasspath() {
    RuntimeEnvironment environment = new RuntimeEnvironment(currentJavaHome(), fakeWorkingDirectory(), systemClasspath(), systemClasspath(), fakeBuildPaths(), systemClasspath());
    String classpath = environment.getRunnerFullClassPath();
    assertTrue(classpath, classpath.contains("infinitest"));
    assertTrue(classpath, classpath.endsWith(environment.findInfinitestRunnerJar()));
  }

  @Test
  public void shouldLogErrorIfInfinitestJarCannotBeFound() {
    LoggingAdapter listener = new LoggingAdapter();
    addLoggingListener(listener);

    RuntimeEnvironment environment = emptyRuntimeEnvironment();
    environment.getRunnerFullClassPath();
    assertTrue(listener.hasMessage("Could not find a classpath entry for infinitest-runner in infinitest-runner.classpath", SEVERE));
  }

  @Test
  public void canSetAdditionalVMArguments() {
    RuntimeEnvironment environment = fakeEnvironment();
    List<String> additionalArgs = asList("-Xdebug", "-Xnoagent", "-Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=y");
    environment.addVMArgs(additionalArgs);
    List<String> actualArgs = environment.createProcessArguments(new File("file.classpath"));
    assertTrue(actualArgs.toString(), actualArgs.containsAll(additionalArgs));
  }

  @Test
  public void canUseACustomWorkingDirectory() throws Exception {
    runner.setRuntimeEnvironment(new RuntimeEnvironment(currentJavaHome(), new File("src"), FakeEnvironments.systemClasspath(), FakeEnvironments.systemClasspath(), fakeBuildPaths(), FakeEnvironments.systemClasspath()));
    runTests(WorkingDirectoryVerifier.class);
    eventAssert.assertTestPassed(WorkingDirectoryVerifier.class);
  }

  public static class WorkingDirectoryVerifier {
    @Test
    public void shouldFailIfRunningInADifferentWorkingDirectory() {
      assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
      assertTrue(new File("").getAbsoluteFile().getAbsolutePath().endsWith("src"));
    }
  }

  @Override
  protected void waitForCompletion() throws InterruptedException {
    eventAssert.assertRunComplete();
  }
}
