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

import static org.infinitest.CoreDependencySupport.FAILING_TEST;
import static org.infinitest.CoreDependencySupport.PASSING_TEST;
import static org.infinitest.CoreDependencySupport.createAsyncCore;
import static org.infinitest.CoreDependencySupport.withChangedFiles;
import static org.infinitest.CoreDependencySupport.withTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.TestJUnit4TestCase;

class WhenTestsAreRunAsynchronously {
	private DefaultInfinitestCore core;
	private EventSupport eventSupport;

	@BeforeEach
	void inContext() {
		core = createAsyncCore(withChangedFiles(), withTests(FAILING_TEST, PASSING_TEST, TestJUnit4TestCase.class));
		eventSupport = new EventSupport(5000);
		core.addTestQueueListener(eventSupport);
		core.addTestResultsListener(eventSupport);
		// Not sure why, but maven fails if we don't reset the interrupted state
		// here!
		Thread.interrupted();
	}

	@Test
	void canUpdateWhileTestsAreRunning() throws Exception {
		core.update();
		eventSupport.assertQueueChanges(1);
		// There are three tests in the queue, we're updating before they're all
		// finished
		core.update();

		eventSupport.assertQueueChanges(3);
		eventSupport.assertRunComplete();
	}
}
