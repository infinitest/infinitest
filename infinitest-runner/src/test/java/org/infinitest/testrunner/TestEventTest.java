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

import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import jdave.test.*;

import org.assertj.core.api.Assertions;
import org.infinitest.util.*;
import org.junit.*;
import org.junit.rules.*;

import com.google.common.base.*;

public class TestEventTest extends EqualityTestSupport {
	private TestEvent event;
	private Throwable error;

	@Rule
	public TestName testName = new TestName();

	@Before
	public void inContext() {
		try {
			throw new UnserializableException("Exception Message");
		} catch (UnserializableException e) {
			error = e;
		}
		event = eventWithError(error);
	}

	@SuppressWarnings("serial")
	public class UnserializableException extends RuntimeException {
		public UnserializableException(String string) {
			super(string);
		}

		private final OutputStream ostream = System.out;

		{
			assertNotNull(ostream);
		}
	}

	@Test
	public void shouldHandleUnserializeableExceptions() throws Exception {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
		objStream.writeObject(event);
	}

	@Test
	public void shouldStoreExceptionClassesAsStrings() {
		assertEquals(UnserializableException.class.getSimpleName(), event.getErrorClassName());
	}

	@Test
	public void shouldFilterJunitElementsFromPointOfFailure() {
		try {
			fail();
		} catch (AssertionError e) {
			error = e;
			event = eventWithError(e);
			verifyPointOfFailureMessage(e.getStackTrace()[2].getLineNumber());
		}
	}

	@Test
	public void shouldSupportAssertJAssertions() {
		try {
			Assertions.assertThat(true).isFalse();
		} catch (ComparisonFailure e) {
			error = e;
			event = eventWithError(e);
			verifyPointOfFailureMessage(e.getStackTrace()[3].getLineNumber());
		}
	}

	@Test
	public void shouldFilterJdaveElementsFromPointOfFailure() {
		error = JDaveUtils.createException();
		event = eventWithError(error);
		verifyPointOfFailureMessage(error.getStackTrace()[1].getLineNumber());
	}

	@Test
	public void shouldHaveUserPresentableToString() {
		assertEquals(event.getTestName() + "." + event.getTestMethod(), event.toString());
	}

	@Test
	public void shouldProvideFullErrorClassName() {
		assertEquals("java.lang.RuntimeException", methodFailed("", "", new RuntimeException()).getFullErrorClassName());
	}

	@Test
	public void shouldSupportExceptionsWithoutStackTrace() {
		methodFailed("", "", new ExceptionWithoutStackTrace()).getPointOfFailure();
	}

	private void verifyPointOfFailureMessage(int lineNumber) {
		String actual = event.getPointOfFailure().toString();
		String expected = TestEventTest.class.getName() + ":" + lineNumber + " - " + error.getClass().getSimpleName() + "(" + Strings.nullToEmpty(error.getMessage()) + ")";

		Assertions.assertThat(actual).isEqualTo(expected);
	}

	@Override
	protected Object createEqualInstance() {
		return testCaseStarting(getClass().getName());
	}

	@Override
	protected Object createUnequalInstance() {
		return testCaseStarting(Object.class.getName());
	}

	@Override
	protected List<Object> createUnequalInstances() {
		List<Object> unequals = super.createUnequalInstances();
		unequals.add(methodFailed("boo", getClass().getName(), "testMethod", new RuntimeException()));
		return unequals;
	}

	private TestEvent eventWithError(Throwable error) {
		return new TestEvent(METHOD_FAILURE, error.getMessage(), TestEventTest.class.getName(), testName.getMethodName(), error);
	}

	@SuppressWarnings("serial")
	private static class ExceptionWithoutStackTrace extends RuntimeException {
		@Override
		public synchronized Throwable fillInStackTrace() {
			return this;
		}
	}
}
