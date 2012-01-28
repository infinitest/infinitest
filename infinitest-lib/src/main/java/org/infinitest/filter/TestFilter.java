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

/**
 * This filter prevents any matching tests from being run as part of a core
 * update.
 * 
 * @author bjrady
 */
public interface TestFilter {
	/**
	 * Forces an update of the filter, if controlled by an external resource.
	 * This may be necessary if a test class has been removed or added from the
	 * dependency graph.
	 */
	void updateFilterList();

	/**
	 * Check if a test class (identified by the fully qualified name) should be
	 * removed from the test run.
	 * 
	 * @return <code>true</code> if the test should not be run
	 * @see Class#getName()
	 */
	boolean match(String className);
}