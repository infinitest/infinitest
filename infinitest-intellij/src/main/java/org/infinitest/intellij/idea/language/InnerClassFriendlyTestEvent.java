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
package org.infinitest.intellij.idea.language;

import org.infinitest.testrunner.*;

public class InnerClassFriendlyTestEvent {
	private final TestEvent event;

	public InnerClassFriendlyTestEvent(TestEvent event) {
		this.event = event;
	}

	@Override
	public int hashCode() {
		return event.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof InnerClassFriendlyTestEvent)) {
			return false;
		}
		InnerClassFriendlyTestEvent otherEvent = (InnerClassFriendlyTestEvent) other;
		return event.equals(otherEvent.event);
	}

	public String getPointOfFailureClassName() {
		String className = event.getPointOfFailure().getClassName();
		if (className.contains("$")) {
			return className.substring(0, className.indexOf("$"));
		}
		return className;
	}

	public int getPointOfFailureLineNumber() {
		return event.getPointOfFailure().getLineNumber();
	}

	public String getMessage() {
		return event.getMessage();
	}

	public String getErrorClassName() {
		return event.getErrorClassName();
	}

	public String getTestName() {
		return event.getTestName();
	}

	public String getTestMethod() {
		return event.getTestMethod();
	}
}
