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

import static org.infinitest.CoreStatus.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.Mockito.*;

import org.infinitest.*;
import org.infinitest.filter.*;
import org.junit.*;

import com.fakeco.fakeproduct.simple.*;

public class WhenRunningTests {
  private static EventSupport eventHistory;
  private static ResultCollector collector;

  @BeforeClass
  public static void inContext() throws InterruptedException {
    if (eventHistory == null) {
      InfinitestCoreBuilder builder = new InfinitestCoreBuilder(fakeEnvironment(), new FakeEventQueue());
      builder.setUpdateSemaphore(mock(ConcurrencyController.class));

      TestFilter testFilter = mock(TestFilter.class);
      when(testFilter.match(not(startsWith("com.fakeco.fakeproduct.simple")))).thenReturn(true);
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
  public void canListenForResultEvents() {
    eventHistory.assertTestFailed(FailingTest.class);
    eventHistory.assertTestPassed(PassingTest.class);
  }

  // This will frequently hang when something goes horribly wrong in the test
  // runner
  @Test(timeout = 5000)
  public void canListenForChangesToTheTestQueue() throws Exception {
    eventHistory.assertQueueChanges(3);
  }

  @Test
  public void canCollectStateUsingAResultCollector() {
    assertEquals(1, collector.getFailures().size());
    assertEquals(FAILING, collector.getStatus());
  }
}