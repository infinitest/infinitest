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
package org.infinitest.testrunner;

import org.infinitest.MissingClassException;
import org.infinitest.TestClassInitializationException;
import org.infinitest.config.FileBasedInfinitestConfigurationSource;
import org.infinitest.config.InfinitestConfigurationSource;
import org.infinitest.testrunner.junit4.Junit4And3Runner;
import org.infinitest.testrunner.junit5.Junit5Runner;
import org.infinitest.testrunner.testng.TestNgRunner;

/**
 * A proxy which delegates actual test execution JUnit or TestNG.
 *
 */
public class DefaultRunner implements NativeRunner {
	private InfinitestConfigurationSource configSource = FileBasedInfinitestConfigurationSource
			.createFromCurrentWorkingDirectory();

	// to override the default config source for unit tests
	@Override
	public void setTestConfigurationSource(InfinitestConfigurationSource configurationSource) {
		configSource = configurationSource;
	}

	@Override
	public TestResults runTest(String testClassName) {
		Class<?> testClass;
		try {
			testClass = Class.forName(testClassName);
		} catch (ClassNotFoundException e) {
			throw new MissingClassException(testClassName);
		} catch (Throwable t) {
			throw new TestClassInitializationException("Failed to initialize test class " + testClassName, t);
		}

		if (TestNgRunner.isTestNGTest(testClass)) {
			return new TestNgRunner(configSource).runTest(testClass);
		} else if (Junit5Runner.isJUnit5Test(testClass)) {
			return new Junit5Runner(configSource).runTest(testClass);
		} else {
			return new Junit4And3Runner(configSource).runTest(testClass);
		}
	}

}
