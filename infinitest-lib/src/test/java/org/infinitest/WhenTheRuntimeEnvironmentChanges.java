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

import static org.infinitest.CoreDependencySupport.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.infinitest.changedetect.*;
import org.infinitest.parser.*;
import org.infinitest.testrunner.*;
import org.junit.*;

public class WhenTheRuntimeEnvironmentChanges {
	@Test
	public void shouldTriggerACompleteReloadInTheCore() throws Exception {
		InfinitestCore core = createCore(withNoChangedFiles(), withNoTestsToRun());
		EventSupport eventSupport = new EventSupport();
		core.addTestQueueListener(eventSupport);
		core.setRuntimeEnvironment(fakeEnvironment());
		eventSupport.assertReloadOccured();
	}

	@Test
	public void shouldUpdateSupportingClassesInTheCore() {
		RuntimeEnvironment environment = fakeEnvironment();
		TestRunner testRunner = mock(TestRunner.class);
		TestDetector testDetector = mock(TestDetector.class);
		ChangeDetector changeDetector = mock(ChangeDetector.class);

		DefaultInfinitestCore core = new DefaultInfinitestCore(testRunner, new FakeEventQueue());
		core.setTestDetector(testDetector);
		core.setChangeDetector(changeDetector);
		core.setRuntimeEnvironment(environment);

		verify(testRunner).setRuntimeEnvironment(environment);
		verify(testRunner).addTestResultsListener(any(TestResultsListener.class));
		verify(testRunner).setTestPriority(any(Comparator.class));
		verify(testDetector).clear();
		verify(testDetector).setClasspathProvider(environment);
		verify(changeDetector).setClasspathProvider(environment);
		verify(changeDetector).clear();
	}

	@Test
	public void shouldDoNothingIfEnvironmentIsNotActuallyDifferent() throws Exception {
		InfinitestCore core = createCore(withNoChangedFiles(), withNoTestsToRun());
		EventSupport eventSupport = new EventSupport();
		core.addTestQueueListener(eventSupport);
		core.setRuntimeEnvironment(emptyRuntimeEnvironment());
		eventSupport.assertReloadOccured();
		assertEquals(1, eventSupport.getReloadCount());

		core.setRuntimeEnvironment(emptyRuntimeEnvironment());
		assertEquals(1, eventSupport.getReloadCount());
	}
}
