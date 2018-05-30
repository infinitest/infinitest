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

import static com.google.common.collect.Lists.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

public class WhenComparingRuntimeEnvironments {
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

	private RuntimeEnvironment createEnv(String outputDir, String workingDir, String classpath, String javahome) {
		RuntimeEnvironment env = new RuntimeEnvironment(new File(javahome), new File(workingDir), "runnerClassLoaderClassPath", "runnerProcessClassPath", newArrayList(new File(outputDir)), classpath);
		return env;
	}

	private RuntimeEnvironment createEqualInstance() {
		return createEnv("outputDir", "workingDir", "classpath", "javahome");
	}
}
