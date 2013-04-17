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

import java.util.*;

import org.infinitest.testrunner.*;

public class TestEventEqualityAdapter {
	private final TestEvent event;

	public TestEventEqualityAdapter(TestEvent event) {
		this.event = event;
	}

	public TestEvent getEvent() {
		return event;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TestEventEqualityAdapter) {
			TestEventEqualityAdapter other = (TestEventEqualityAdapter) obj;
			return com.google.common.base.Objects.equal(event, other.event) && com.google.common.base.Objects.equal(event.getMessage(), other.event.getMessage()) && com.google.common.base.Objects.equal(event.getPointOfFailure(), other.event.getPointOfFailure()) && Arrays.equals(event.getStackTrace(), other.event.getStackTrace());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(event) ^ com.google.common.base.Objects.hashCode(event.getMessage()) ^ com.google.common.base.Objects.hashCode(event.getPointOfFailure()) ^ Arrays.hashCode(event.getStackTrace());
	}
}