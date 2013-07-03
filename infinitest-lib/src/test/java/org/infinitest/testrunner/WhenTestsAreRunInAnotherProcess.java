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

import static java.util.logging.Level.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.infinitest.ConsoleOutputListener.OutputType.*;
import static org.infinitest.CoreDependencySupport.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.infinitest.testrunner.TestRunnerMother.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.mockito.Mockito.*;

import org.infinitest.*;
import org.infinitest.util.*;
import org.junit.*;

public class WhenTestsAreRunInAnotherProcess extends AbstractRunnerTest {
  private EventSupport eventSupport;
  private AbstractTestRunner runner;
  public boolean outputPrinted;

  @Before
  public void inContext() {
    eventSupport = new EventSupport(5000);
    runner = createRunner();
    runner.addTestResultsListener(eventSupport);
    runner.addTestQueueListener(eventSupport);
    outputPrinted = false;
  }

  @AfterClass
  public static void resetStatefulCounter() {
    StubStatefulTest.counter = 0;
  }

  @Test
  public void shouldHandleLargeAmountsOfConsoleOutput() throws Exception {
    final StringBuffer stdOut = new StringBuffer();
    final StringBuffer stdErr = new StringBuffer();
    runner = new MultiProcessRunner();
    runner.setRuntimeEnvironment(fakeEnvironment());
    runner.addTestQueueListener(eventSupport);
    runner.addConsoleOutputListener(new ConsoleListenerAdapter() {
      @Override
      public void consoleOutputUpdate(String newText, ConsoleOutputListener.OutputType outputType) {
        if (outputType.equals(STDOUT)) {
          stdOut.append(newText);
        } else {
          stdErr.append(newText);
        }
      }
    });
    // DEBT Race condition in this test. We need to check the status of
    // stdOut and stdErr in the
    // consoleOutputUpdate method and notify a lock that we can hold in the
    // test instead of just
    // assuming that all the output will be ready by the time the tests
    // finish running. But
    // I don't have the brain cells for that right now.

    runner.runTest(TestWithLotsOfConsoleOutput.class.getName());
    waitForCompletion();

    assertThat(stdOut.toString()).contains("Hello").excludes("World");
    assertThat(stdErr.toString()).contains("World").excludes("Hello");
  }

  @Test
  public void shouldHandleVeryLongClasspath() throws Exception {
    runner.setRuntimeEnvironment(fakeVeryLongClasspathEnvironment());
    runTest(StubStatefulTest.class.getName());
    eventSupport.assertTestsStarted(StubStatefulTest.class);
    eventSupport.assertTestPassed(StubStatefulTest.class);
  }

  public static class TestWithLotsOfConsoleOutput {
    @Test
    public void shouldFail() {
      assumeTrue(testIsBeingRunFromInfinitest());
      for (int i = 0; i < 10; i++) {
        System.out.println("Hello");
        System.err.println("World");
      }
    }
  }

  @Test
  public void individualTestRunsAreIndependent() throws Exception {
    runTest(StubStatefulTest.class.getName());
    eventSupport.assertTestsStarted(StubStatefulTest.class);
    eventSupport.assertTestPassed(StubStatefulTest.class);

    eventSupport.reset();
    runTest(StubStatefulTest.class.getName());
    eventSupport.assertTestsStarted(StubStatefulTest.class);
    eventSupport.assertTestPassed(StubStatefulTest.class);
  }

  @Test
  public void canControlParallelUpdatesOfCores() throws Exception {
    ConcurrencyController controller = mock(ConcurrencyController.class);

    runner.setConcurrencyController(controller);
    runTest(PASSING_TEST.getName());

    verify(controller).acquire();
    verify(controller).release();
  }

  @Test
  public void shouldFireStartingEventBeforeTestIsActuallyStarted() throws Exception {
    runTest("thisIsNotATest");
    eventSupport.assertEventsReceived(TEST_CASE_STARTING, METHOD_FAILURE);
  }

  @Test
  public void subsequentTestRunAreIndependent() throws Exception {
    runTest(PASSING_TEST.getName());
    eventSupport.assertTestsStarted(PASSING_TEST);
    eventSupport.assertTestPassed(PASSING_TEST);
    eventSupport.reset();

    runTest(FAILING_TEST.getName());
    eventSupport.assertTestsStarted(FAILING_TEST);
    eventSupport.assertTestFailed(FAILING_TEST);
  }

  @Test
  public void shouldGroupTestRunsTogether() throws Exception {
    runTests(StubStatefulTest.class, OtherStubStatefulTest.class);
    eventSupport.assertTestsStarted(StubStatefulTest.class, OtherStubStatefulTest.class);
    eventSupport.assertTestPassed(StubStatefulTest.class);
    eventSupport.assertTestFailed(OtherStubStatefulTest.class);
  }

  @Test
  public void shouldLogWarningIfClasspathContainsMissingFilesOrDirectories() {
    LoggingAdapter adapter = new LoggingAdapter();
    addLoggingListener(adapter);
    emptyRuntimeEnvironment().getCompleteClasspath();
    String expectedMessage = "Could not find classpath entry [classpath] at file system root or relative to working " + "directory [.].";
    assertTrue(adapter.toString(), adapter.hasMessage(expectedMessage, WARNING));
  }

  public static class OtherStubStatefulTest {
    @Test
    public void incrementStaticVariable() {
      assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
      StubStatefulTest.counter++;
      assertEquals(1, StubStatefulTest.counter);
    }
  }

  public static class StubStatefulTest {
    private static int counter = 0;

    @Test
    public void incrementStaticVariable() {
      assumeTrue(testIsBeingRunFromInfinitest());
      counter++;
      assertEquals(1, counter);
    }
  }

  @Override
  protected AbstractTestRunner getRunner() {
    return runner;
  }

  @Override
  protected void waitForCompletion() throws InterruptedException {
    eventSupport.assertRunComplete();
  }
}
