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

import static org.infinitest.environment.FakeEnvironments.currentJavaHome;
import static org.infinitest.environment.FakeEnvironments.fakeBuildPaths;
import static org.infinitest.testrunner.TestRunnerMother.createRunner;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;

import org.infinitest.EventSupport;
import org.infinitest.config.FileBasedInfinitestConfigurationSource;
import org.infinitest.environment.FakeEnvironments;
import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.util.InfinitestTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WhenRunningTestsInDifferentEnvironments extends AbstractRunnerTest {
  private EventSupport eventAssert;
  private AbstractTestRunner runner;
  boolean outputPrinted;
  protected boolean runComplete;
  @TempDir
  private File fakeJavaHome;

  @BeforeEach
  void inContext() {
    eventAssert = new EventSupport();
    runner = createRunner();
    runner.addTestResultsListener(eventAssert);
    runner.addTestQueueListener(eventAssert);
    outputPrinted = false;
    new File(fakeJavaHome, "bin").mkdirs();
  }

  @Override
  protected AbstractTestRunner getRunner() {
    return runner;
  }

 

  @Test
  void canUseACustomWorkingDirectory() throws Exception {
    runner.setRuntimeEnvironment(new RuntimeEnvironment(currentJavaHome(),
        new File("src"),
        FakeEnvironments.systemClasspath(),
        FakeEnvironments.systemClasspath(),
        fakeBuildPaths(),
        FakeEnvironments.systemClasspath(),
        FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory()));
    runTests(WorkingDirectoryVerifier.class);
    eventAssert.assertTestPassed(WorkingDirectoryVerifier.class);
  }

  static class WorkingDirectoryVerifier {
    @Test
    void shouldFailIfRunningInADifferentWorkingDirectory() {
      assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
      assertTrue(new File("").getAbsoluteFile().getAbsolutePath().endsWith("src"));
    }
  }

  @Override
  protected void waitForCompletion() throws InterruptedException {
    eventAssert.assertRunComplete();
  }
}
