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

import static com.google.common.collect.Lists.*;
import static java.util.logging.Level.*;
import static org.apache.commons.lang.StringUtils.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.infinitest.parser.*;

import com.google.common.base.*;
import com.google.common.io.*;

public class RegexFileFilter implements TestFilter {
	private final File file;
	private final List<Pattern> filters = newArrayList();
	private boolean isInverted = false;

	public RegexFileFilter(File file) {
		this.file = file;
		if (!file.exists()) {
			log(INFO, "Filter file " + file + " does not exist.");
		}
		updateFilterList();
	}

	@Override
	public boolean match(JavaClass javaClass) {
		String className = javaClass.getName();
		for (Pattern pattern : filters) {
			if (pattern.matcher(className).lookingAt()) {
				return !isInverted;
			}
		}
		return isInverted;
	}

	@Override
	public void updateFilterList() {
		filters.clear();
		this.isInverted = false;

		if (file.exists()) {
			readFilterFile();
		}
	}

	private void readFilterFile() {
		try {

			for (String line : Files.readLines(file, Charsets.UTF_8)) {

				if (filters.isEmpty() && isWhitelistFilterOption(line)) {
					this.isInverted = true;
				} else if (isValidFilter(line)) {
					filters.add(Pattern.compile(line));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Something horrible happened to the filter file", e);
		}
	}

	private boolean isWhitelistFilterOption(String line) {
		return !isBlank(line) && line.startsWith("!whitelist");
	}

	private boolean isValidFilter(String line) {
		return !isBlank(line) && !line.startsWith("!") && !line.startsWith("#");
	}

}
