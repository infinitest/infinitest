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

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TestRunConfigurator {
	private static final String SUFFIX = "\\s?=\\s?(.+)";
	private static final String PREFIX = "^\\s*#+\\s?";
	private static final String EXCLUDED_GROUPS = "excluded-groups";
	private static final String INCLUDED_GROUPS = "groups";
	private static final String LISTENERS = "listeners";
	private static final Pattern EXCLUDED = Pattern.compile(PREFIX + EXCLUDED_GROUPS + SUFFIX);
	private static final Pattern INCLUDED = Pattern.compile(PREFIX + INCLUDED_GROUPS + SUFFIX);
	private static final Pattern LISTENER = Pattern.compile(PREFIX + LISTENERS + SUFFIX);
	private static final Pattern EXCLUDED_CATEGORIES = Pattern.compile(PREFIX + "excluded-categories" + SUFFIX);
	private static final File FILTERFILE = new File("infinitest.filters");

	private final TestNGConfiguration testNGConfiguration;
	private final JUnitConfiguration junitConfiguration;
	private final File file;

	public TestRunConfigurator() {
		this(FILTERFILE);
	}

	public TestRunConfigurator(File filterFile) {
		testNGConfiguration = new TestNGConfiguration();
		junitConfiguration = new JUnitConfiguration();
		file = filterFile;

		updateFilterList();
	}

	public void updateFilterList() {
		if (file == null) {
			return;
		}

		if (file.exists()) {
			tryToReadFilterFile();
		}
	}

	public TestNGConfiguration getTestNGConfig() {
		return testNGConfiguration;
	}

	public JUnitConfiguration getJUnitConfig() {
		return junitConfiguration;
	}

	private void tryToReadFilterFile() {
		try {
			readFilterFile();
		} catch (IOException e) {
			throw new RuntimeException("Something horrible happened to the filter file", e);
		}
	}

	private void readFilterFile() throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			do {
				line = reader.readLine();
				if (line != null) {
					addFilter(line);
				}
			} while (line != null);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void addFilter(String line) {
		addTestNGFilter(line);
		addJUnitFilter(line);
	}

	private void addJUnitFilter(String line) {
		Matcher matcher = EXCLUDED_CATEGORIES.matcher(line.trim());

		if (!matcher.matches()) {
			return;
		}

		String excludedCategoriesConfig = matcher.group(1);

		String[] excludedCategoryNames = excludedCategoriesConfig.split(",");

		List<Class<?>> excludedCategories = asClasses(excludedCategoryNames);

		junitConfiguration.setExcludedCategories(excludedCategories);
	}

	private List<Class<?>> asClasses(String[] classNames) {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		for (String name : classNames) {
			try {
				classes.add(Class.forName(name));
			} catch (ClassNotFoundException e) {
				// ignore
				// or should we throw a MissingClassException?
			}
		}

		return classes;
	}

	private void addTestNGFilter(String line) {
		Matcher matcher = EXCLUDED.matcher(line.trim());
		if (matcher.matches()) {
			String excludedGroups = matcher.group(1);
			testNGConfiguration.setExcludedGroups(excludedGroups);
		} else {
			matcher = INCLUDED.matcher(line);
			if (matcher.matches()) {
				String includedGroups = matcher.group(1).trim();
				testNGConfiguration.setGroups(includedGroups);
			} else {
				matcher = LISTENER.matcher(line);
				if (matcher.matches()) {
					final List<Object> listenerList = createListenerList(matcher.group(1).trim());
					testNGConfiguration.setListeners(listenerList);
				}
			}
		}
	}

	private List<Object> createListenerList(String listeners) {
		final String[] listenerTypes = listeners.split("\\s*,\\s*");
		final List<Object> listenerList = new ArrayList<Object>();
		for (final String listenername : listenerTypes) {
			try {
				listenerList.add(Class.forName(listenername).newInstance());
			} catch (InstantiationException e) {
				// unable to add this listener, just continue with the next.
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// unable to add this listener, just continue with the next.
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// unable to add this listener, just continue with the next.
				e.printStackTrace();
			}
		}
		return listenerList;
	}
}
