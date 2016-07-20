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
package org.infinitest.filter;

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;
import java.util.*;
import java.util.Optional;
import java.util.regex.*;

import org.infinitest.parser.*;

import com.google.common.base.*;
import com.google.common.base.Objects;
import com.google.common.io.*;

public class RegexFileFilter implements TestFilter {

	private final File filterFile;
	private final Set<FilterEntry> includeFilterEntries = new LinkedHashSet<FilterEntry>();
	private final Set<FilterEntry> excludeFilterEntries = new LinkedHashSet<FilterEntry>();

	public RegexFileFilter(File file) {
		this.filterFile = file;
		if (!file.exists()) {
			log(INFO, "Filter file " + file + " does not exist.");
		}
		updateFilterList();
	}

	@Override
	public void updateFilterList() {

		includeFilterEntries.clear();
		excludeFilterEntries.clear();

		try {
			List<String> lines = Files.readLines(filterFile, Charsets.UTF_8);
			for (String line : lines) {

				Optional<FilterEntry> filterEntry = readyFilterEntry(line);
				if (filterEntry.isPresent()) {
					addFilterEntry(filterEntry.get());
				}

			}

		} catch (IOException e) {
			log(WARNING, "failed to read filter file: " + filterFile);
		}
	}

	private void addFilterEntry(FilterEntry filterEntry) {
		if (filterEntry.including) {
			includeFilterEntries.add(filterEntry);
		} else {
			excludeFilterEntries.add(filterEntry);
		}
	}

	private Optional<FilterEntry> readyFilterEntry(String line) {

		if (!isValidFilterLine(line)) {
			return Optional.empty();
		}

		boolean including = line.startsWith("include=");
		String expression = line;
		if (line.contains("=")) {
			expression = line.substring(line.indexOf("=") + 1);
		}

		return Optional.of(new FilterEntry(including, expression));
	}

	private boolean isValidFilterLine(String line) {
		return !Strings.isNullOrEmpty(line) && !line.startsWith("!") && !line.startsWith("#");
	}

	@Override
	public boolean match(JavaClass javaClass) {

		if (!includeFilterEntries.isEmpty() && !matchesFilters(javaClass, includeFilterEntries)) {
			return true;
		}

		return matchesFilters(javaClass, excludeFilterEntries);
	}

	private boolean matchesFilters(JavaClass javaClass, Set<FilterEntry> entries) {
		for (FilterEntry entry : entries) {
			if (entry.matches(javaClass.getName())) {
				return true;
			}
		}
		return false;
	}

	private static class FilterEntry {

		final boolean including;
		final String expression;
		final Pattern pattern;

		FilterEntry(boolean including, String expression) {
			this.including = including;
			this.expression = expression;
			this.pattern = Pattern.compile(expression);
		}

		public boolean matches(String name) {
			Matcher matcher = pattern.matcher(name);
			return matcher.matches() || matcher.lookingAt();
		}

		@Override
		public String toString() {
			return expression;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(including, expression);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			FilterEntry other = (FilterEntry) obj;
			return Objects.equal(including, other.including) && Objects.equal(expression, other.expression);
		}

	}

}
