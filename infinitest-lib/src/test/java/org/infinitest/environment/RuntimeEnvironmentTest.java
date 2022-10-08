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
package org.infinitest.environment;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Files.touch;
import static java.util.Arrays.asList;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.infinitest.environment.FakeEnvironments.currentJavaHome;
import static org.infinitest.environment.FakeEnvironments.emptyRuntimeEnvironment;
import static org.infinitest.environment.FakeEnvironments.fakeBuildPaths;
import static org.infinitest.environment.FakeEnvironments.fakeEnvironment;
import static org.infinitest.environment.FakeEnvironments.fakeWorkingDirectory;
import static org.infinitest.environment.FakeEnvironments.systemClasspath;
import static org.infinitest.util.InfinitestUtils.addLoggingListener;
import static org.infinitest.util.InfinitestUtils.convertFromWindowsClassPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.infinitest.environment.RuntimeEnvironment.JavaHomeException;
import org.infinitest.util.LoggingAdapter;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.io.Files;

public class RuntimeEnvironmentTest {

	@Rule
	public FakeJavaHomeRule fakeJavaHomeRule = new FakeJavaHomeRule();

	@Test
	public void shouldCompareEqualEnvironments() {
		assertEquals(createEqualInstance(), createEqualInstance());
		assertEquals(createEqualInstance().hashCode(), createEqualInstance().hashCode());
	}

	@Test
	public void shouldCompareOutputDirectories() {
		RuntimeEnvironment env = createEnv("notTheSameOutputDir", "workingDir", "classpath", "javahome");
		assertThat(createEqualInstance(), not(equalTo(env)));
		assertThat(createEqualInstance().hashCode(), not(equalTo(env.hashCode())));
	}

	@Test
	public void shouldCompareWorkingDirectory() {
		RuntimeEnvironment env = createEnv("outputDir", "notTheSameWorkingDir", "classpath", "javahome");
		assertThat(createEqualInstance(), not(equalTo(env)));
	}

	@Test
	public void shouldCompareClasspath() {
		RuntimeEnvironment env = createEnv("outputDir", "workingDir", "notTheSameClasspath", "javahome");
		assertThat(createEqualInstance(), not(equalTo(env)));
	}
	
	@Test
	public void shouldCompareJavaHome() {
		RuntimeEnvironment env = createEnv("outputDir", "workingDir", "classpath", "notTheSameJavahome");
		assertThat(createEqualInstance(), not(equalTo(env)));
	}

	@Test
	public void shouldCompareAdditionalArgs() {
		RuntimeEnvironment env = createEqualInstance();
		env.addVMArgs(Arrays.asList("additionalArg"));
		assertThat(createEqualInstance(), not(equalTo(env)));
	}

	@Test
	public void shouldNotBeEqualToNull() {
		assertFalse(createEqualInstance().equals(null));
	}

	@Test
	public void shouldThrowExceptionOnInvalidJavaHome() {
		RuntimeEnvironment environment = new RuntimeEnvironment(fakeJavaHomeRule.getRoot(), fakeWorkingDirectory(),
				"runnerClassLoaderClassPath", "runnerProcessClassPath", fakeBuildPaths(),
				FakeEnvironments.systemClasspath());
		try {
			ClasspathArgumentBuilder classpathArgumentBuilder = mock(ClasspathArgumentBuilder.class);
			environment.createProcessArguments(classpathArgumentBuilder);
			fail("Should have thrown exception");
		} catch (JavaHomeException e) {
			assertThat(convertFromWindowsClassPath(e.getMessage()))
					.contains(convertFromWindowsClassPath(fakeJavaHomeRule.getRoot().getAbsolutePath()) + "/bin/java");
		}
	}

	@Test
	public void shouldAllowAlternateJavaHomesOnUnixAndWindows() throws Exception {
		RuntimeEnvironment environment = new RuntimeEnvironment(fakeJavaHomeRule.getRoot(), fakeWorkingDirectory(),
				"runnerClassLoaderClassPath", "runnerProcessClassPath", fakeBuildPaths(),
				FakeEnvironments.systemClasspath());

		touch(new File(fakeJavaHomeRule.getRoot(), "bin/java.exe"));
		ClasspathArgumentBuilder classpathArgumentBuilder = mock(ClasspathArgumentBuilder.class);
		List<String> arguments = environment.createProcessArguments(classpathArgumentBuilder);
		assertEquals(convertFromWindowsClassPath(fakeJavaHomeRule.getRoot().getAbsolutePath()) + "/bin/java.exe",
				convertFromWindowsClassPath(get(arguments, 0)));

		touch(new File(fakeJavaHomeRule.getRoot(), "bin/java"));
		arguments = environment.createProcessArguments(classpathArgumentBuilder);
		assertEquals(convertFromWindowsClassPath(fakeJavaHomeRule.getRoot().getAbsolutePath()) + "/bin/java",
				convertFromWindowsClassPath(get(arguments, 0)));
	}

	@Test
	public void shouldAddInfinitestJarOrClassDirToClasspath() {
		RuntimeEnvironment environment = new RuntimeEnvironment(currentJavaHome(), fakeWorkingDirectory(),
				systemClasspath(), systemClasspath(), fakeBuildPaths(), systemClasspath());
		String classpath = environment.getRunnerFullClassPath();
		assertTrue(classpath, classpath.contains("infinitest"));
		assertTrue(classpath, classpath.endsWith(environment.findInfinitestRunnerJar()));
	}

	@Test
	public void shouldUseInfinitestClassLoaderForProcessClassPath() {
		LoggingAdapter listener = new LoggingAdapter();
		addLoggingListener(listener);

		RuntimeEnvironment environment = new RuntimeEnvironment(currentJavaHome(), fakeWorkingDirectory(),
				systemClasspath(), systemClasspath(), fakeBuildPaths(), systemClasspath());
		Map<String, String> env = environment.createProcessEnvironment();
		assertThat(env).containsEntry("CLASSPATH", environment.getRunnerBootstrapClassPath());
	}

	@Test(expected = RuntimeEnvironment.MissingInfinitestClassLoaderException.class)
	public void shouldThrowIfInfinitestClassLoaderJarCannotBeFound() {
		LoggingAdapter listener = new LoggingAdapter();
		addLoggingListener(listener);

		RuntimeEnvironment environment = emptyRuntimeEnvironment();
		environment.createProcessEnvironment();
	}

	@Test(expected = RuntimeEnvironment.MissingInfinitestRunnerException.class)
	public void shouldThrowIfInfinitestRunnerJarCannotBeFound() {
		LoggingAdapter listener = new LoggingAdapter();
		addLoggingListener(listener);

		RuntimeEnvironment environment = emptyRuntimeEnvironment();
		environment.getRunnerFullClassPath();
	}

	@Test
	public void shouldLogWarningIfClasspathContainsMissingFilesOrDirectories() {
		RuntimeEnvironment environment = new RuntimeEnvironment(currentJavaHome(), 
				fakeWorkingDirectory(),
				systemClasspath(), systemClasspath(), fakeBuildPaths(), "classpath");
		LoggingAdapter adapter = new LoggingAdapter();
		addLoggingListener(adapter);
		environment.getRunnerFullClassPath();
		String expectedMessage = "Could not find classpath entry [classpath] at file system root or relative to working "
				+ "directory [.].";
		assertTrue(adapter.toString(), adapter.hasMessage(expectedMessage, WARNING));
	}

	@Test
	public void canSetAdditionalVMArguments() {
		RuntimeEnvironment environment = fakeEnvironment();
		List<String> additionalArgs = asList("-Xdebug", "-Xnoagent",
				"-Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=y");
		environment.addVMArgs(additionalArgs);

		ClasspathArgumentBuilder classpathArgumentBuilder = mock(ClasspathArgumentBuilder.class);
		List<String> actualArgs = environment.createProcessArguments(classpathArgumentBuilder);
		assertTrue(actualArgs.toString(), actualArgs.containsAll(additionalArgs));
	}

	@Test
	public void shouldCreateClasspathFile() {
		File classpathFile = fakeEnvironment().createClasspathFile();
		assertThat(classpathFile).exists();
	}

	private RuntimeEnvironment createEnv(String outputDir, String workingDir, String classpath, String javahome) {
		RuntimeEnvironment env = new RuntimeEnvironment(new File(javahome), new File(workingDir),
				"runnerClassLoaderClassPath", "runnerProcessClassPath", newArrayList(new File(outputDir)), classpath);
		return env;
	}

	private RuntimeEnvironment createEqualInstance() {
		return createEnv("outputDir", "workingDir", "classpath", "javahome");
	}
	
	@Test
	public void escapeClassPathArgumentfile() throws IOException {
		String classpath = "c:\\Program Files (x86)\\Java\\jre\\lib\\ext;c:\\Program Files\\Java\\jre9\\lib\\ext";
		RuntimeEnvironment env = new RuntimeEnvironment(new File("javahome"),
				new File("workingDir"),
				"runnerClassLoaderClassPath",
				"runnerProcessClassPath",
				Collections.singletonList(new File("outputDir")),
				classpath) {
			@Override
			String findInfinitestRunnerJar() {
				return "c:/test path/runner.jar";
			}
		};
		
		File file = env.createClasspathArgumentFile();
		
		String content = Files.toString(file, StandardCharsets.UTF_8);
		
		assertThat(content).isEqualTo("\"c:\\\\Program Files (x86)\\\\Java\\\\jre\\\\lib\\\\ext;c:\\\\Program Files\\\\Java\\\\jre9\\\\lib\\\\ext"
				+ File.pathSeparator
				// not sure why we get the \r\n with Guava but not with java.nio
				+ "c:/test path/runner.jar\"\r\n");
	}
}

