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
import static com.google.common.io.Files.touch;
import static java.util.Arrays.asList;
import static java.util.logging.Level.WARNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.infinitest.environment.FakeEnvironments.currentJavaHome;
import static org.infinitest.environment.FakeEnvironments.emptyRuntimeEnvironment;
import static org.infinitest.environment.FakeEnvironments.fakeBuildPaths;
import static org.infinitest.environment.FakeEnvironments.fakeEnvironment;
import static org.infinitest.environment.FakeEnvironments.fakeWorkingDirectory;
import static org.infinitest.environment.FakeEnvironments.systemClasspath;
import static org.infinitest.util.InfinitestUtils.addLoggingListener;
import static org.infinitest.util.InfinitestUtils.convertFromWindowsClassPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.infinitest.config.FileBasedInfinitestConfigurationSource;
import org.infinitest.environment.RuntimeEnvironment.JavaHomeException;
import org.infinitest.util.LoggingAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.Files;

class RuntimeEnvironmentTest {

	@TempDir
	private File javaHome;
	
	@BeforeEach
	void setup() {
		new File(javaHome, "bin").mkdirs();
	}

	@Test
	void shouldCompareEqualEnvironments() {
		assertEquals(createEqualInstance(), createEqualInstance());
		assertEquals(createEqualInstance().hashCode(), createEqualInstance().hashCode());
	}

	@Test
	void shouldCompareOutputDirectories() {
		RuntimeEnvironment env = createEnv("notTheSameOutputDir", "workingDir", "classpath", "javahome");
		assertThat(createEqualInstance()).isNotEqualTo(env);
		assertThat(createEqualInstance().hashCode()).isNotEqualTo(env.hashCode());
	}

	@Test
	void shouldCompareWorkingDirectory() {
		RuntimeEnvironment env = createEnv("outputDir", "notTheSameWorkingDir", "classpath", "javahome");
		assertThat(createEqualInstance()).isNotEqualTo(env);
	}

	@Test
	void shouldCompareClasspath() {
		RuntimeEnvironment env = createEnv("outputDir", "workingDir", "notTheSameClasspath", "javahome");
		assertThat(createEqualInstance()).isNotEqualTo(env);
	}
	
	@Test
	void shouldCompareJavaHome() {
		RuntimeEnvironment env = createEnv("outputDir", "workingDir", "classpath", "notTheSameJavahome");
		assertThat(createEqualInstance()).isNotEqualTo(env);
	}

	@Test
	void shouldCompareAdditionalArgs() {
		RuntimeEnvironment env = createEqualInstance();
		env.addVMArgs(Arrays.asList("additionalArg"));
		assertThat(createEqualInstance()).isNotEqualTo(env);
	}

	@Test
	void shouldNotBeEqualToNull() {
		assertNotEquals(null, createEqualInstance());
	}

	@Test
	void shouldThrowExceptionOnInvalidJavaHome() {
		RuntimeEnvironment environment = new RuntimeEnvironment(javaHome,
				fakeWorkingDirectory(),
				"runnerClassLoaderClassPath",
				"runnerProcessClassPath",
				fakeBuildPaths(),
				FakeEnvironments.systemClasspath(),
				FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory());
		try {
			ClasspathArgumentBuilder classpathArgumentBuilder = mock(ClasspathArgumentBuilder.class);
			environment.createProcessArguments(classpathArgumentBuilder);
			fail("Should have thrown exception");
		} catch (JavaHomeException e) {
			assertThat(convertFromWindowsClassPath(e.getMessage()))
					.contains(convertFromWindowsClassPath(javaHome.getAbsolutePath()) + "/bin/java");
		}
	}

	@Test
	void shouldAllowAlternateJavaHomesOnUnixAndWindows() throws Exception {
		RuntimeEnvironment environment = new RuntimeEnvironment(javaHome,
				fakeWorkingDirectory(),
				"runnerClassLoaderClassPath",
				"runnerProcessClassPath",
				fakeBuildPaths(),
				FakeEnvironments.systemClasspath(),
				FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory());

		touch(new File(javaHome, "bin/java.exe"));
		ClasspathArgumentBuilder classpathArgumentBuilder = mock(ClasspathArgumentBuilder.class);
		List<String> arguments = environment.createProcessArguments(classpathArgumentBuilder);
		assertEquals(convertFromWindowsClassPath(javaHome.getAbsolutePath()) + "/bin/java.exe",
				convertFromWindowsClassPath(get(arguments, 0)));

		touch(new File(javaHome, "bin/java"));
		arguments = environment.createProcessArguments(classpathArgumentBuilder);
		assertEquals(convertFromWindowsClassPath(javaHome.getAbsolutePath()) + "/bin/java",
				convertFromWindowsClassPath(get(arguments, 0)));
	}

	@Test
	void shouldAddInfinitestJarOrClassDirToClasspath() {
		RuntimeEnvironment environment = new RuntimeEnvironment(
				currentJavaHome(),
				fakeWorkingDirectory(),
				systemClasspath(),
				systemClasspath(),
				fakeBuildPaths(),
				systemClasspath(),
				FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory());
		String classpath = environment.getRunnerFullClassPath();
		assertTrue(classpath.contains("infinitest"));
		assertTrue(classpath.endsWith(environment.findInfinitestRunnerJar()), classpath);
	}

	@Test
	void shouldUseInfinitestClassLoaderForProcessClassPath() {
		LoggingAdapter listener = new LoggingAdapter();
		addLoggingListener(listener);

		RuntimeEnvironment environment = new RuntimeEnvironment(
				currentJavaHome(),
				fakeWorkingDirectory(),
				systemClasspath(),
				systemClasspath(),
				fakeBuildPaths(),
				systemClasspath(),
				FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory());
		Map<String, String> env = environment.createProcessEnvironment();
		assertThat(env).containsEntry("CLASSPATH", environment.getRunnerBootstrapClassPath());
	}

	@Test
	void shouldThrowIfInfinitestClassLoaderJarCannotBeFound() {
		LoggingAdapter listener = new LoggingAdapter();
		addLoggingListener(listener);

		RuntimeEnvironment environment = emptyRuntimeEnvironment();
		assertThatThrownBy(() -> environment.createProcessEnvironment()).isInstanceOf(RuntimeEnvironment.MissingInfinitestClassLoaderException.class);
	}

	@Test
	void shouldThrowIfInfinitestRunnerJarCannotBeFound() {
		LoggingAdapter listener = new LoggingAdapter();
		addLoggingListener(listener);

		RuntimeEnvironment environment = emptyRuntimeEnvironment();
		assertThatThrownBy(() -> environment.getRunnerFullClassPath()).isInstanceOf(RuntimeEnvironment.MissingInfinitestRunnerException.class);
	}

	@Test
	void shouldLogWarningIfClasspathContainsMissingFilesOrDirectories() {
		RuntimeEnvironment environment = new RuntimeEnvironment(currentJavaHome(),
				fakeWorkingDirectory(),
				systemClasspath(),
				systemClasspath(),
				fakeBuildPaths(),
				"classpath",
				FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory());
		LoggingAdapter adapter = new LoggingAdapter();
		addLoggingListener(adapter);
		environment.getRunnerFullClassPath();
		String expectedMessage = "Could not find classpath entry [classpath] at file system root or relative to working "
				+ "directory [.].";
		assertTrue(adapter.hasMessage(expectedMessage, WARNING), adapter.toString());
	}

	@Test
	void canSetAdditionalVMArguments() {
		RuntimeEnvironment environment = fakeEnvironment();
		List<String> additionalArgs = asList("-Xdebug", "-Xnoagent",
				"-Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=y");
		environment.addVMArgs(additionalArgs);

		ClasspathArgumentBuilder classpathArgumentBuilder = mock(ClasspathArgumentBuilder.class);
		List<String> actualArgs = environment.createProcessArguments(classpathArgumentBuilder);
		assertTrue(actualArgs.containsAll(additionalArgs), actualArgs.toString());
	}

	@Test
	void shouldCreateClasspathFile() {
		File classpathFile = fakeEnvironment().createClasspathFile();
		assertThat(classpathFile).exists();
	}

	private RuntimeEnvironment createEnv(String outputDir, String workingDir, String classpath, String javahome) {
		return new RuntimeEnvironment(new File(javahome),
				new File(workingDir),
				"runnerClassLoaderClassPath",
				"runnerProcessClassPath",
				asList(new File(outputDir)),
				classpath,
				FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory());
	}

	private RuntimeEnvironment createEqualInstance() {
		return createEnv("outputDir", "workingDir", "classpath", "javahome");
	}
	
	@Test
	void escapeClassPathArgumentfile() throws IOException {
		String classpath = "c:\\Program Files (x86)\\Java\\jre\\lib\\ext;c:\\Program Files\\Java\\jre9\\lib\\ext";
		RuntimeEnvironment env = new RuntimeEnvironment(new File("javahome"),
				new File("workingDir"),
				"runnerClassLoaderClassPath",
				"runnerProcessClassPath",
				Collections.singletonList(new File("outputDir")),
				classpath,
				FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory()) {
			@Override
			String findInfinitestRunnerJar() {
				return "c:/test path/runner.jar";
			}
		};
		
		File file = env.createClasspathArgumentFile();
		
		String content = Files.toString(file, StandardCharsets.UTF_8).trim();
		
		assertThat(content).isEqualTo("\"c:\\\\Program Files (x86)\\\\Java\\\\jre\\\\lib\\\\ext;c:\\\\Program Files\\\\Java\\\\jre9\\\\lib\\\\ext"
				+ File.pathSeparator
				+ "c:/test path/runner.jar\"");
	}
}

