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

import static com.google.common.collect.Maps.*;
import static java.lang.System.*;

import java.util.*;

import org.infinitest.*;

public class RunStatistics extends TestResultsAdapter {
	private final Map<String, Long> failureTimestamps;

	public RunStatistics() {
		failureTimestamps = newHashMap();
	}

	private void update(TestEvent event) {
		failureTimestamps.put(event.getTestName(), currentTimeMillis());
	}

	public long getLastFailureTime(String testName) {
		if (!failureTimestamps.containsKey(testName)) {
			return 0;
		}
		return failureTimestamps.get(testName);
	}

	@Override
	public void testCaseComplete(TestCaseEvent event) {
		for (TestEvent each : event.getFailureEvents()) {
			update(each);
		}
	}
}
