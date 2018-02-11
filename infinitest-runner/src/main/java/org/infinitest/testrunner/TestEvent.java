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

import java.io.*;

/**
 * @author <a href="mailto:benrady@gmail.com">Ben Rady</a>
 */
public class TestEvent implements Serializable {
	// Strange serialization errors occur (consistently) if we don't include
	// this. I'm not sure why.
	private static final long serialVersionUID = -1821340797590868333L;

	public enum TestState {
		METHOD_FAILURE, TEST_CASE_STARTING
	}

	private final String message;
	private final String name;
	private final String method;
	private final TestState state;
	private boolean isAssertionFailure;
	private StackTraceElement[] stackTrace;
	private String simpleErrorClassName;
	private String fullErrorClassName;

	public TestEvent(TestState eventType, String message, String testName, String testMethod, Throwable error) {
		this.message = message;
		name = testName;
		method = testMethod;
		state = eventType;

		if (error != null) {
			populateAttributesToEnsureSerializability(error);
		}
	}

	public static TestEvent methodFailed(String message, String testName, String methodName, Throwable throwable) {
		return new TestEvent(METHOD_FAILURE, message, testName, methodName, throwable);
	}

	public static TestEvent methodFailed(String testName, String methodName, Throwable throwable) {
		return new TestEvent(METHOD_FAILURE, throwable.getMessage(), testName, methodName, throwable);
	}

	public static TestEvent testCaseStarting(String testClass) {
		return new TestEvent(TEST_CASE_STARTING, "Test Starting", testClass, "", null);
	}

	private void populateAttributesToEnsureSerializability(Throwable error) {
		isAssertionFailure = isTestFailure(error);
		stackTrace = error.getStackTrace();
		simpleErrorClassName = error.getClass().getSimpleName();
		fullErrorClassName = error.getClass().getName();
	}

  private static boolean isTestFailure(Throwable exception) {
    return exception instanceof AssertionError;
  }

	public String getMessage() {
		return null == message ? "" : message;
	}

	public String getTestName() {
		return name;
	}

	public String getTestMethod() {
		return method;
	}

	public boolean isFailure() {
		return isAssertionFailure;
	}

	public TestState getType() {
		return state;
	}

	@Override
	public String toString() {
		return name + "." + method;
	}

	private String getPointOfFailureClass() {
		if (stackTrace.length == 0) // Temporary fix
		{
			return fullErrorClassName;
		}
		return getPointOfFailureElement().getClassName();
	}

	private int getPointOfFailureLineNumber() {
		if (stackTrace.length == 0) // Temporary fix
		{
			return 0;
		}
		return getPointOfFailureElement().getLineNumber();
	}

	private StackTraceElement getPointOfFailureElement() {
		for (StackTraceElement element : stackTrace) {
			if (element.getMethodName().equals(method) && element.getClassName().equals(name)) {
				return element;
			}
		}
		return stackTrace[0];
	}

	public PointOfFailure getPointOfFailure() {
		if (getErrorClassName() == null) {
			return null;
		}

		return new PointOfFailure(getPointOfFailureClass(), getPointOfFailureLineNumber(), getErrorClassName(), getMessage());
	}

	@Override
	public int hashCode() {
		// Can't use generateEquals here because this class needs to remain
		// serializable, and
		// InfinitestUtils uses 3rd party libraries
		return name.hashCode() ^ method.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TestEvent) {
			TestEvent other = (TestEvent) obj;
			return safeEquals(name, other.name) && safeEquals(method, other.method);
		}
		return false;
	}

	// Can't use infinitestUtils.safeEquals here because this class needs to
	// remain serializable,
	// and InfinitestUtils uses 3rd party libraries
	private static boolean safeEquals(Object orig, Object other) {
		if ((orig == null) && (other == null)) {
			return true;
		}
		if ((orig == null) || (other == null)) {
			return false;
		}

		return orig.equals(other);
	}

	public String getErrorClassName() {
		return simpleErrorClassName;
	}

	public String getFullErrorClassName() {
		return fullErrorClassName;
	}

	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}
}
