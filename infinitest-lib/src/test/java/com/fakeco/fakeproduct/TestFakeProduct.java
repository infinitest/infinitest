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
package com.fakeco.fakeproduct;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * This is a test who's behavior can be externally controlled using a set of
 * static methods.
 *
 * It uses a property file because there's no way to get the instance of the
 * class that JUnit creates and we couldn't use a static variable because JUnit
 * (used to!) use a different classloader.
 *
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 *
 */
public class TestFakeProduct {
	private static final String CALLCOUNT_POSTFIX = ".callcount";
	private static final String EXCEPTION_POSTFIX = ".exception";
	@SuppressWarnings("unused") private FakeProduct fakeProduct = null;
	private static File statFailureStateFile = new File("TestFakeProduct.data");

	@Before
	public void setUp() throws Exception {
		if (!statFailureStateFile.exists()) {
			setUpState();
			statFailureStateFile.deleteOnExit();
		}
		fakeProduct = new FakeProduct();
		runTestMethod("setUp");
	}

	@After
	public void tearDown() throws Exception {
		fakeProduct = null;
		runTestMethod("tearDown");
	}

	@Test
	public void testNumber1() throws Exception {
		runTestMethod("testNumber1");
		@SuppressWarnings("unused") Object o = new Runnable() {
			@Override
			public void run() {
				throw new IllegalStateException("This is an inner class and should not be run");
			}
		};
	}

	@Test
	public void testNumber2() throws Exception {
		runTestMethod("testNumber2");
	}

	public static void setTestSuccess(String testName, String failureMessage, boolean pass) throws FileNotFoundException, IOException {
		if (pass) {
			writeProperty(testName, "");
		} else {
			writeProperty(testName, failureMessage);
		}
	}

	public static int getCallCount(String methodName) throws FileNotFoundException, IOException {
		Properties prop = loadProperties();
		String callCount = prop.getProperty(methodName + CALLCOUNT_POSTFIX);
		if (callCount == null) {
			return 0;
		}
		return Integer.parseInt(callCount);
	}

	public static void setUpState() throws IOException {
		tearDownState();
		assertThat(statFailureStateFile.createNewFile())
                .as("Could not re-initialize test state")
                .isTrue();
	}

	public static void tearDownState() {
		if (statFailureStateFile.exists()) {
			statFailureStateFile.deleteOnExit();
			statFailureStateFile.delete();
		}
	}

	private void runTestMethod(String methodName) throws Exception {
		int callcount = getCallCount(methodName);
		writeProperty(methodName + CALLCOUNT_POSTFIX, Integer.toString(callcount + 1));
		String message = readProperty(methodName);
		if ((message != null) && !message.equals("")) {
			if (message.endsWith(EXCEPTION_POSTFIX)) {
				String exception = message.replace(EXCEPTION_POSTFIX, "");
				Class<?> clazz = Class.forName(exception);
				throw (Exception) clazz.newInstance();
			}
			fail(message);
		}
	}

	private static Properties loadProperties() throws FileNotFoundException, IOException {
		if (!statFailureStateFile.exists()) {
			throw new IllegalStateException("You must call setUpState before using other methods on this class");
		}
		Properties properties = new Properties();
		InputStream inputStream = new FileInputStream(statFailureStateFile);
		try {
			properties.load(inputStream);
		} finally {
			inputStream.close();
		}
		return properties;
	}

	private String readProperty(String testName) throws FileNotFoundException, IOException {
		Properties prop = loadProperties();
		String message = prop.getProperty(testName);
		return message;
	}

	private static void writeProperty(String key, String value) throws FileNotFoundException, IOException {
		if (!statFailureStateFile.exists()) {
			throw new IllegalStateException("You must call clearState before using other methods on this class");
		}
		Properties properties = loadProperties();
		properties.setProperty(key, value);
		FileOutputStream fostream = new FileOutputStream(statFailureStateFile);
		properties.store(fostream, "");
		fostream.close();
	}

	public static void setTestError(String testName, Class<?> exceptionClass) throws FileNotFoundException, IOException {
		if (exceptionClass == null) {
			writeProperty(testName, "");
		} else {
			writeProperty(testName, exceptionClass.getName() + EXCEPTION_POSTFIX);
		}
	}
}
