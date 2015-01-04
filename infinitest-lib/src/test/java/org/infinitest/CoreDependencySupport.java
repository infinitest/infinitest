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

import static org.infinitest.testrunner.TestRunnerMother.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assume.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import org.infinitest.changedetect.*;
import org.infinitest.parser.*;
import org.infinitest.testrunner.*;
import org.infinitest.util.*;
import org.junit.*;

import com.fakeco.fakeproduct.*;
import com.fakeco.fakeproduct.simple.*;

public class CoreDependencySupport {
	public static final Class<?> FAILING_TEST = FailingTest.class;
	public static final Class<?> PASSING_TEST = PassingTest.class;

	public static class SlowTest {
		@Test
		public void shouldBeReallySlow() throws Exception {
			assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
			Thread.sleep(1000000);
		}
	}

	private CoreDependencySupport() {
		// nothing to do here
	}

	public static TestDetector withTests(final Class<?>... testClasses) {
		return new StubTestDetector() {
			@Override
			public Set<JavaClass> findTestsToRun(Collection<File> changedFiles) {
				Set<JavaClass> testsToRun = new HashSet<JavaClass>();
				if (!isCleared()) {
					for (Class<?> each : testClasses) {
						JavaClass javaClass = mock(JavaClass.class);
						when(javaClass.getName()).thenReturn(each.getName());
						testsToRun.add(javaClass);
					}
				}

				return testsToRun;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}
		};
	}

	public static TestDetector withNoTestsToRun() {
		return mock(TestDetector.class);
	}

	public static ChangeDetector withChangedFiles(Class<?>... changedClasses) {
		if (changedClasses.length == 0) {
			createChangeDetector(TestFakeProduct.class);
		}
		return createChangeDetector(changedClasses);
	}

	private static ChangeDetector createChangeDetector(Class<?>... changedClasses) {
		Set<File> changedFiles = new HashSet<File>();
		for (Class<?> each : changedClasses) {
			changedFiles.add(getFileForClass(each));
		}
		return new FakeChangeDetector(changedFiles, false);
	}

	public static ChangeDetector withNoChangedFiles() {
		return createChangeDetector();
	}

	static DefaultInfinitestCore createCore(ChangeDetector changedFiles, TestDetector tests) {
		return createCore(changedFiles, tests, new FakeEventQueue());
	}

	static DefaultInfinitestCore createCore(ChangeDetector changedFiles, TestDetector tests, EventQueue eventQueue) {
		DefaultInfinitestCore core = new DefaultInfinitestCore(new InProcessRunner(), eventQueue);
		core.setChangeDetector(changedFiles);
		core.setTestDetector(tests);
		return core;
	}

	static DefaultInfinitestCore createAsyncCore(ChangeDetector changeDetector, TestDetector testDetector) {
		DefaultInfinitestCore core = new DefaultInfinitestCore(createRunner(), new FakeEventQueue());
		core.setChangeDetector(changeDetector);
		core.setTestDetector(testDetector);
		return core;
	}
}
