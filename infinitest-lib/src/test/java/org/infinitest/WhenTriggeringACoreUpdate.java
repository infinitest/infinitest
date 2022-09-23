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

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.infinitest.changedetect.ChangeDetector;
import org.infinitest.parser.JavaClass;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.TestRunner;
import org.junit.Before;
import org.junit.Test;

public class WhenTriggeringACoreUpdate {
	private Set<File> updatedFiles;
	private DefaultInfinitestCore core;
	private ChangeDetector changeDetector;
	private TestDetector testDetector;

	@Before
	public void inContext() throws IOException {
		updatedFiles = new HashSet<>();
		core = new DefaultInfinitestCore(mock(TestRunner.class), new ControlledEventQueue());
		
		changeDetector = mock(ChangeDetector.class);
		when(changeDetector.findChangedFiles()).thenReturn(updatedFiles);
		core.setChangeDetector(changeDetector);
		
		testDetector = mock(TestDetector.class);
		when(testDetector.getCurrentTests()).thenReturn(Collections.<String> emptySet());
		core.setTestDetector(testDetector);
	}

	private void testsToExpect(JavaClass... tests) {
		when(testDetector.findTestsToRun(updatedFiles)).thenReturn(newHashSet(tests));
	}

	@Test
	public void canUseAKnownListOfChangedFilesToReduceFileSystemAccess() {
		testsToExpect();
		core.update(updatedFiles);
	}

	@Test
	public void shouldReturnTheNumberOfTestsRun() {
		JavaClass javaClass = mock(JavaClass.class);

		testsToExpect(javaClass);

		assertEquals(1, core.update(updatedFiles));
	}
}
