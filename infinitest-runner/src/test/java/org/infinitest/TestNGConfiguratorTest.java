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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.infinitest.config.InfinitestConfiguration;
import org.infinitest.config.MemoryInfinitestConfigurationSource;
import org.junit.jupiter.api.Test;
import org.testng.ITestNGListener;

class TestNGConfiguratorTest {

	@Test
	void emptyConfigShouldConvertToEmptyExcludedGroups() {
		TestNGConfiguration config = fromInfinitestConfig(InfinitestConfiguration.empty());
		assertThat(config.getExcludedGroups()).isEmpty();
	}

	@Test
	void emptyConfigShouldConvertToEmptyGroups() {
		TestNGConfiguration config = fromInfinitestConfig(InfinitestConfiguration.empty());
		assertThat(config.getGroups()).isEmpty();
	}

	@Test
	void emptyConfigShouldConvertToEmptyListeners() {
		TestNGConfiguration config = fromInfinitestConfig(InfinitestConfiguration.empty());
		assertThat(config.getListeners()).isEqualTo(Collections.emptyList());
	}

	@Test
	void groupsShouldBeJoinedAsACommaSeparatedList() {
		TestNGConfiguration config = fromInfinitestConfig(InfinitestConfiguration.builder().includedGroups("quick", "smoketests").build());
		assertThat(config.getGroups()).isEqualTo("quick,smoketests");
	}

	@Test
	void excludedGroupsShouldBeJoinedAsACommaSeparatedList() {
		TestNGConfiguration config = fromInfinitestConfig(InfinitestConfiguration.builder().excludedGroups("slow", "broken", "manual").build());
		assertThat(config.getExcludedGroups()).isEqualTo("slow,broken,manual");
	}

	@Test
	void listenersClassesShouldBeInstantiated() {
		TestNGConfiguration config = fromInfinitestConfig(InfinitestConfiguration.builder().testngListeners("org.testng.internal.annotations.DefaultAnnotationTransformer", "org.testng.reporters.JUnitXMLReporter").build());
		List<ITestNGListener> listeners = config.getListeners();

		assertThat(listeners.get(0)).isExactlyInstanceOf(org.testng.internal.annotations.DefaultAnnotationTransformer.class);
		assertThat(listeners.get(1)).isExactlyInstanceOf(org.testng.reporters.JUnitXMLReporter.class);
	}

	@Test
	void unknowListenersClassesShouldBeIgnored() {
		TestNGConfiguration config = fromInfinitestConfig(InfinitestConfiguration.builder().testngListeners("org.testng.UnknownReporter", "org.testng.reporters.JUnitXMLReporter").build());
		List<ITestNGListener> listeners = config.getListeners();

		assertThat(listeners.get(0)).isExactlyInstanceOf(org.testng.reporters.JUnitXMLReporter.class);
	}

	private TestNGConfiguration fromInfinitestConfig(InfinitestConfiguration infinitestConfig) {
		return new TestNGConfigurator(new MemoryInfinitestConfigurationSource(infinitestConfig)).readConfig();
	}

}
