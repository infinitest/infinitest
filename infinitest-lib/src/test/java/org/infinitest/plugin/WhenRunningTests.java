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
package org.infinitest.plugin;

import static org.infinitest.CoreStatus.FAILING;
import static org.infinitest.environment.FakeEnvironments.fakeEnvironment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.infinitest.ConcurrencyController;
import org.infinitest.EventSupport;
import org.infinitest.FakeEventQueue;
import org.infinitest.InfinitestCore;
import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.ResultCollector;
import org.infinitest.filter.TestFilter;
import org.infinitest.parser.JavaClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.ArgumentMatcher;

import com.fakeco.fakeproduct.simple.FailingTest;
import com.fakeco.fakeproduct.simple.PassingTest;

class WhenRunningTests {
  private static EventSupport eventHistory;
  private static ResultCollector collector;

  @BeforeAll
  static void inContext() throws InterruptedException {
    if (eventHistory == null) {
      InfinitestCoreBuilder builder = new InfinitestCoreBuilder(fakeEnvironment(), new FakeEventQueue(), "myCoreName");
      builder.setUpdateSemaphore(mock(ConcurrencyController.class));

      TestFilter testFilter = mock(TestFilter.class);
      when(testFilter.match(not(argThat(startsWith("com.fakeco.fakeproduct.simple"))))).thenReturn(true);
      builder.setFilter(testFilter);

      InfinitestCore core = builder.createCore();
      collector = new ResultCollector(core);

      eventHistory = new EventSupport();
      core.addTestResultsListener(eventHistory);
      core.addTestQueueListener(eventHistory);

      core.update();
      eventHistory.assertRunComplete();
    }
  }

  @Test
  void canListenForResultEvents() {
    eventHistory.assertTestFailed(FailingTest.class);
    eventHistory.assertTestPassed(PassingTest.class);
  }

  // This will frequently hang when something goes horribly wrong in the test
  // runner
  @Timeout(5)
  @Test
  void canListenForChangesToTheTestQueue() throws Exception {
    eventHistory.assertQueueChanges(3);
  }

  @Test
  void canCollectStateUsingAResultCollector() {
    assertEquals(1, collector.getFailures().size());
    assertEquals(FAILING, collector.getStatus());
  }

  static ArgumentMatcher<JavaClass> startsWith(final String namePrefix) {
    return new ArgumentMatcher<JavaClass>() {
      @Override
      public boolean matches(JavaClass argument) {
        return argument.getName().startsWith(namePrefix);
      }
    };
  }
}