/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import org.infinitest.parser.*;
import org.infinitest.testrunner.*;
import org.junit.*;

public class WhenTriggeringACoreUpdate {
	private List<File> updatedFiles;
	private DefaultInfinitestCore core;
	private TestDetector testDetector;

	@Before
	public void inContext() {
		updatedFiles = newArrayList();
		core = new DefaultInfinitestCore(mock(TestRunner.class), new ControlledEventQueue());
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
		testsToExpect(new FakeJavaClass("FakeTest"));

		assertEquals(1, core.update(updatedFiles));
	}
}
