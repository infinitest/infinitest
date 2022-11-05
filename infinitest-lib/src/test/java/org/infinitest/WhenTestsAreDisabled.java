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
import static org.infinitest.CoreDependencySupport.withChangedFiles;
import static org.infinitest.util.InfinitestUtils.setify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.infinitest.parser.JavaClass;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.TestRunner;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.simple.PassingTest;

class WhenTestsAreDisabled {
	@SuppressWarnings("unchecked")
	@Test
	void shouldFireAppropriateEvent() {
		TestRunner runner = mock(TestRunner.class);
		TestDetector testDetector = mock(TestDetector.class);
		when(testDetector.getCurrentTests()).thenReturn(setify("MyClass", "OtherClass"), setify("OtherClass"));
		Set<JavaClass> emptyClassSet = Collections.<JavaClass> emptySet();
		when(testDetector.findTestsToRun(any(Collection.class))).thenReturn(emptyClassSet);

		DefaultInfinitestCore core = new DefaultInfinitestCore(runner, new ControlledEventQueue());
		core.setChangeDetector(withChangedFiles(PassingTest.class));
		core.setTestDetector(testDetector);
		final Set<String> disabledTestList = new HashSet<>();
		core.addDisabledTestListener(new DisabledTestListener() {
			@Override
			public void testsDisabled(Collection<String> testName) {
				disabledTestList.addAll(testName);
			}
		});
		core.update();
		assertEquals("MyClass", getOnlyElement(disabledTestList));
	}
}
