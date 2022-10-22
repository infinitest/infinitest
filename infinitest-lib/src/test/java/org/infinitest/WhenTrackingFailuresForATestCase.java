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
package org.infinitest;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collection;

import org.infinitest.testrunner.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenTrackingFailuresForATestCase {
  private TestCaseFailures failures;
  private TestEvent newEvent;

  @BeforeEach
  void inContext() {
    TestEvent method1Event = eventFor("method1");
    failures = new TestCaseFailures(asList(method1Event, eventFor("method2")));
    failures.addNewFailure(method1Event);
  }

  @Test
  void shouldIgnoreStrictlyEqualUpdates() {
    assertThat(failures.updatedFailures()).isEmpty();
  }

  @Test
  void shouldOnlyUpdateFailuresIfNotStrictlyEqual() {
    newEvent = failedWithMessage("method2", "anotherMessage");
    failures.addNewFailure(newEvent);
    Collection<TestEvent> updatedFailures = failures.updatedFailures();
    assertSame(newEvent, getOnlyElement(updatedFailures));
  }

  @Test
  void shouldDetermineWhichFailuresAreNew() {
    failures.addNewFailure(eventFor("method3"));
    Collection<TestEvent> newFailures = failures.newFailures();

    assertThat(newFailures).containsOnly(eventFor("method3"));
  }

  @Test
  void shouldDetermineWhichFailuresHaveBeenRemoved() {
    Collection<TestEvent> removedFailures = failures.removedFailures();
    assertEquals(eventFor("method2"), getOnlyElement(removedFailures));
  }

  private TestEvent eventFor(String methodName) {
    return failedWithMessage(methodName, "message");
  }

  private TestEvent failedWithMessage(String methodName, String message) {
    return methodFailed(message, "testName", methodName, new AssertionError(methodName + " failed"));
  }
}
