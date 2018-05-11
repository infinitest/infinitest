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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Objects;

public class TestCaseEvent {
	private final List<TestEvent> methodEvents;
	private final Object source;
	private final String testName;
	private final TestResults results;

	public TestCaseEvent(String testName, Object source, TestResults results) {
		this.testName = testName;
		this.source = source;
		this.results = results;
		methodEvents = newArrayList();
		// DEBT move this to a static factory method?
		for (TestEvent testEvent : results) {
			if (!isCompilationErrors(testEvent)) {
				methodEvents.add(testEvent);
			}
		}
	}

	private boolean isCompilationErrors(TestEvent testEvent) {
		return Objects.equals(testEvent.getFullErrorClassName(), VerifyError.class.getName())
				|| isUnresolvedCompilationProblemsError(testEvent);
	}

	private boolean isUnresolvedCompilationProblemsError(TestEvent testEvent) {
		// This is the kind of error launched at runtime if code with compile errors are
		// executed
		return Objects.equals(testEvent.getFullErrorClassName(), Error.class.getName())
				&& testEvent.getMessage() != null
				&& testEvent.getMessage().contains("Unresolved compilation problems:");
	}

	public boolean failed() {
		return !getFailureEvents().isEmpty();
	}

	public Object getSource() {
		return source;
	}

	public String getTestName() {
		return testName;
	}

	public List<TestEvent> getFailureEvents() {
		return unmodifiableList(methodEvents);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TestCaseEvent) {
			TestCaseEvent other = (TestCaseEvent) obj;
			return testName.equals(other.testName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return testName.hashCode();
	}

	public Iterable<MethodStats> getRunStats() {
		return results.getMethodStats();
	}
}
