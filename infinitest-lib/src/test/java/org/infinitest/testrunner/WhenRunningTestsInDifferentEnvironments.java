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
package org.infinitest.testrunner;

import static com.google.common.collect.Iterables.*;
import static com.google.common.io.Files.*;
import static java.util.Arrays.*;
import static java.util.logging.Level.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.testrunner.TestRunnerMother.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.*;
import java.util.*;

import org.infinitest.*;
import org.infinitest.util.*;
import org.junit.*;

public class WhenRunningTestsInDifferentEnvironments extends AbstractRunnerTest {
	private EventSupport eventAssert;
	private AbstractTestRunner runner;
	public boolean outputPrinted;
	protected boolean runComplete;
	private File fakeJavaHome;

	@Before
	public void inContext() {
		eventAssert = new EventSupport();
		runner = createRunner();
		runner.addTestResultsListener(eventAssert);
		runner.addTestQueueListener(eventAssert);
		outputPrinted = false;
		fakeJavaHome = new File("fakeJavaHome");
		new File(fakeJavaHome, "bin").mkdirs();
	}

	@After
	public void cleanup() throws IOException {
		deleteRecursively(fakeJavaHome);
	}

	@Override
	protected AbstractTestRunner getRunner() {
		return runner;
	}

	@Test
	public void shouldThrowExceptionOnInvalidJavaHome() {
		RuntimeEnvironment environment = new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(), FakeEnvironments.systemClasspath(), fakeJavaHome);
		try {
			environment.createProcessArguments();
			fail("Should have thrown exception");
		} catch (JavaHomeException e) {
			assertThat(convertFromWindowsClassPath(e.getMessage()), containsString(convertFromWindowsClassPath(fakeJavaHome.getAbsolutePath()) + "/bin/java"));
		}
	}

	@Test
	public void shouldAllowAlternateJavaHomesOnUnixAndWindows() throws Exception {
		RuntimeEnvironment environment = new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(), FakeEnvironments.systemClasspath(), fakeJavaHome);

		touch(new File(fakeJavaHome, "bin/java.exe"));
		List<String> arguments = environment.createProcessArguments();
		assertEquals(convertFromWindowsClassPath(fakeJavaHome.getAbsolutePath()) + "/bin/java.exe", convertFromWindowsClassPath(get(arguments, 0)));

		touch(new File(fakeJavaHome, "bin/java"));
		arguments = environment.createProcessArguments();
		assertEquals(convertFromWindowsClassPath(fakeJavaHome.getAbsolutePath()) + "/bin/java", convertFromWindowsClassPath(get(arguments, 0)));
	}

	@Test
	public void shouldAddInfinitestJarOrClassDirToClasspath() {
		RuntimeEnvironment environment = new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(), systemClasspath(), currentJavaHome());
		String classpath = environment.getCompleteClasspath();
		assertTrue(classpath, classpath.contains("infinitest"));
	}

	@Test
	public void shouldLogErrorIfInfinitestJarCannotBeFound() {
		LoggingAdapter listener = new LoggingAdapter();
		addLoggingListener(listener);

		RuntimeEnvironment environment = emptyRuntimeEnvironment();
		environment.setInfinitestRuntimeClassPath("noClasses");
		environment.getCompleteClasspath();
		assertTrue(listener.hasMessage("Could not find a classpath entry for Infinitest Core in noClasses", SEVERE));
	}

	@Test
	public void canSetAdditionalVMArguments() {
		RuntimeEnvironment environment = fakeEnvironment();
		List<String> additionalArgs = asList("-Xdebug", "-Xnoagent", "-Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=y");
		environment.addVMArgs(additionalArgs);
		List<String> actualArgs = environment.createProcessArguments();
		assertTrue(actualArgs.toString(), actualArgs.containsAll(additionalArgs));
	}

	@Test
	public void canUseACustomWorkingDirectory() throws Exception {
		runner.setRuntimeEnvironment(new RuntimeEnvironment(fakeBuildPaths(), new File("src"), FakeEnvironments.systemClasspath(), currentJavaHome()));
		runTests(WorkingDirectoryVerifier.class);
		eventAssert.assertTestPassed(WorkingDirectoryVerifier.class);
	}

	public static class WorkingDirectoryVerifier {
		@Test
		public void shouldFailIfRunningInADifferentWorkingDirectory() {
			assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
			assertTrue(new File("").getAbsoluteFile().getAbsolutePath().endsWith("src"));
		}
	}

	@Override
	protected void waitForCompletion() throws InterruptedException {
		eventAssert.assertRunComplete();
	}
}
