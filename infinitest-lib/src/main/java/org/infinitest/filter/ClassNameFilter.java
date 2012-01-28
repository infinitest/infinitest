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
package org.infinitest.filter;

import static com.google.common.collect.Lists.*;
import static org.apache.commons.lang.StringUtils.*;

import java.util.*;
import java.util.regex.*;

public class ClassNameFilter {
	private List<Pattern> filters;

	public ClassNameFilter() {
		filters = newArrayList();
	}

	protected void clearFilters() {
		filters = newArrayList();
	}

	public boolean match(String className) {
		for (Pattern pattern : filters) {
			if (pattern.matcher(className).lookingAt()) {
				return true;
			}
		}
		return false;
	}

	public void addFilter(String regex) {
		if (isValidFilter(regex)) {
			filters.add(Pattern.compile(regex));
		}
	}

	private boolean isValidFilter(String line) {
		return !isBlank(line) && !line.startsWith("!") && !line.startsWith("#");
	}
}
