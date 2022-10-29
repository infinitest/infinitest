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
package org.infinitest.testrunner.junit5;

import org.infinitest.config.InfinitestConfigurationSource;
import org.infinitest.testrunner.TestResults;
import org.junit.platform.engine.Filter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class Junit5Runner {

	private final InfinitestConfigurationSource configSource;

	public Junit5Runner(InfinitestConfigurationSource configSource) {
		this.configSource = configSource;
	}

	public TestResults runTest(Class<?> clazz) {
		List<Filter<?>> filters = new ArrayList<>();
		if (!configSource.getConfiguration().includedGroups().isEmpty()) {
			filters.add(TagFilter.includeTags(configSource.getConfiguration().includedGroups().asList()));
		}
		if (!configSource.getConfiguration().excludedGroups().isEmpty()) {
			filters.add(TagFilter.excludeTags(configSource.getConfiguration().excludedGroups().asList()));		
		}
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(selectClass(clazz))
				.filters(filters.toArray(new Filter[0]))
				.build();

		Launcher launcher = LauncherFactory.create();

		// Register a listener of your choice
		JUnit5EventTranslator listener = new JUnit5EventTranslator();
		launcher.registerTestExecutionListeners(listener);

		launcher.execute(request);

		return listener.getTestResults();
	}

	public static boolean isJUnit5Test(Class<?> clazz) {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(selectClass(clazz))
				.build();

		try {
			TestPlan plan = LauncherFactory.create().discover(request);
			
			return plan.containsTests();
		} catch (Throwable e) {
			// This might fail for any number of reasons...
			// TODO : use some log instead?
			e.printStackTrace();
			return false;
		}
	}
}
