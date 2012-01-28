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
package org.infinitest.testrunner;

import java.util.*;

import org.infinitest.*;

public interface TestRunner {
	void setTestPriority(Comparator<String> testPriority);

	void runTests(List<String> testNames);

	void addTestResultsListener(TestResultsListener listener);

	void removeTestStatusListener(TestResultsListener listener);

	void setRuntimeEnvironment(RuntimeEnvironment environment);

	void addConsoleOutputListener(ConsoleOutputListener listener);

	void removeConsoleOutputListener(ConsoleOutputListener listener);

	void addTestQueueListener(TestQueueListener listener);

	void removeTestQueueListener(ReloadListener testQueueNormalizer);

	void setConcurrencyController(ConcurrencyController semaphore);
}
