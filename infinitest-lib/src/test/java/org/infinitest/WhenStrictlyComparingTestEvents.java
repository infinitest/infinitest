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

import static org.fest.assertions.Assertions.assertThat;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;

import org.infinitest.testrunner.*;
import org.infinitest.util.*;
import org.junit.*;

public class WhenStrictlyComparingTestEvents extends EqualityTestSupport {
  private Throwable throwable;
  private TestEventEqualityAdapter event1;
  private TestEventEqualityAdapter event2;

  @Before
  public void inContext() {
    throwable = new Throwable();
    throwable.fillInStackTrace();
    event1 = createEqualInstance();
    event2 = createUnequalInstance();
  }

  @Test
  public void shouldIncludeLineNumberInEventComparison() {
    throwable = new Throwable();
    throwable.fillInStackTrace();
    event2 = adapterFor(methodFailed("message1", "testName", "methodName", throwable));

    assertThat(event1).isNotEqualTo(event2);
  }

  @Test
  public void shouldIncludeStackTraceOfFailureInEventComparison() {
    throwable = throwableWithStack(new StackTraceElement("declaringClass", "methodName", "fileName", 1), new StackTraceElement("declaringClass", "anotherMethod", "fileName", 1));

    Throwable anotherThrowable = throwableWithStack(new StackTraceElement("declaringClass", "methodName", "fileName", 1), new StackTraceElement("declaringClass", "anotherMethod", "fileName", 2));

    assertEquals(throwable.getClass(), anotherThrowable.getClass());

    event1 = adapterFor(methodFailed("message1", "testName", "methodName", throwable));
    event2 = adapterFor(methodFailed("message1", "testName", "methodName", anotherThrowable));

    assertThat(event1).isNotEqualTo(event2);
  }

  private TestEventEqualityAdapter adapterFor(TestEvent event) {
    return new TestEventEqualityAdapter(event);
  }

  @Override
  protected TestEventEqualityAdapter createEqualInstance() {
    return adapterFor(methodFailed("message1", "testName", "methodName", throwable));
  }

  @Override
  protected TestEventEqualityAdapter createUnequalInstance() {
    return adapterFor(methodFailed("message2", "testName", "methodName", throwable));
  }
}
