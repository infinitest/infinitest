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
package org.infinitest.util;

import static java.util.Arrays.*;

import java.io.*;
import java.util.*;

import org.infinitest.*;

public class FakeEnvironments {
	public static File fakeClassDirectory() {
		return new File("target/test-classes");
	}

	public static List<File> fakeBuildPaths() {
		return asList(new File("target/classes"), fakeClassDirectory());
	}

	public static ClasspathProvider fakeClasspath() {
		return new StandaloneClasspath(fakeBuildPaths(), systemClasspath());
	}

	public static RuntimeEnvironment fakeEnvironment() {
		return new RuntimeEnvironment(currentJavaHome(), fakeWorkingDirectory(), systemClasspath(), systemClasspath(), fakeBuildPaths(), systemClasspath());
	}

	public static File fakeWorkingDirectory() {
		return new File(".");
	}

	public static File currentJavaHome() {
		return new File(System.getProperty("java.home"));
	}

	public static ClasspathProvider emptyClasspath() {
		return new StandaloneClasspath(asList(new File("thisdirectorydoesnotexist")), "classpath");
	}

	public static RuntimeEnvironment emptyRuntimeEnvironment() {
		return new RuntimeEnvironment(currentJavaHome(), fakeWorkingDirectory(), "infinitest-classloader.classpath", "infinitest-runner.classpath", asList(new File("thisdirectorydoesnotexist")), "classpath");
	}

	public static RuntimeEnvironment fakeVeryLongClasspathEnvironment() {
		return new RuntimeEnvironment(currentJavaHome(), fakeWorkingDirectory(), fakeVeryLongClassPaths(), fakeVeryLongClassPaths(), fakeBuildPaths(), fakeVeryLongClassPaths());
	}

	private static String fakeVeryLongClassPaths() {
		int veryLongClasspathLength = 32768;
		StringBuilder veryLongClassPath = new StringBuilder(veryLongClasspathLength);
		String systemClasspath = systemClasspath();

		while (veryLongClassPath.length() < veryLongClasspathLength) {
			veryLongClassPath.append(systemClasspath).append(File.pathSeparator);
		}

		return veryLongClassPath.toString();
	}

	public static String systemClasspath() {
		// This is a workaround for the maven surefire plugin classpath issue
		// listed here:
		// http://jira.codehaus.org/browse/SUREFIRE-435
		if (System.getProperty("surefire.test.class.path") != null) {
			return System.getProperty("surefire.test.class.path");
		}

		return System.getProperty("java.class.path");
	}
}
