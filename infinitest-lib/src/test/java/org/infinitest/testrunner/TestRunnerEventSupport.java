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

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.*;

import org.infinitest.*;
import org.junit.*;

public class TestRunnerEventSupport {
  private int events;
  private RunnerEventSupport eventSupport;

  @Before
  public void inContext() {
    eventSupport = new RunnerEventSupport(this);
  }

  @Test
  public void shouldFireEventsWhenTestRunFinishes() {
    eventSupport.addTestQueueListener(new TestQueueAdapter() {
      @Override
      public void testRunComplete() {
        events++;
      }
    });
    eventSupport.fireTestRunComplete();
    assertEquals(1, events);
  }

  @Test
  public void shouldFireTestQueueEvents() {
    final List<TestQueueEvent> queueEvents = newArrayList();
    eventSupport.addTestQueueListener(new TestQueueAdapter() {
      @Override
      public void testQueueUpdated(TestQueueEvent event) {
        queueEvents.add(event);
      }
    });
    TestQueueEvent event = new TestQueueEvent(asList("MyTest"), 1);
    eventSupport.fireQueueEvent(event);

    assertThat(queueEvents).contains(event);
  }
}
