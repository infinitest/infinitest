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

import java.util.ArrayList;
import java.util.List;

import org.infinitest.config.FileBasedInfinitestConfigurationSource;
import org.infinitest.config.InfinitestConfiguration;
import org.infinitest.config.InfinitestConfigurationSource;
import org.testng.ITestNGListener;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

public class TestNGConfigurator {
	private InfinitestConfigurationSource configSource;

	public TestNGConfigurator() {
		this(FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory());
	}

	public TestNGConfigurator(InfinitestConfigurationSource configSource) {
		this.configSource = configSource;
	}

	public TestNGConfiguration readConfig() {
		InfinitestConfiguration infinitestConfig = configSource.getConfiguration();

		TestNGConfiguration testNgConfiguration = new TestNGConfiguration();
		testNgConfiguration.setGroups(Joiner.on(',').join(infinitestConfig.includedGroups()));
		testNgConfiguration.setExcludedGroups(Joiner.on(',').join(infinitestConfig.excludedGroups()));

		testNgConfiguration.setListeners(createListenerList(infinitestConfig.testngListeners()));
		return testNgConfiguration;
	}

	private List<ITestNGListener> createListenerList(ImmutableSet<String> listenerClassNames) {
		List<ITestNGListener> listenerList = new ArrayList<>();

		for (String listenerClassName : listenerClassNames) {
			try {
				ITestNGListener listener = (ITestNGListener) Class.forName(listenerClassName).getConstructor().newInstance();
				listenerList.add(listener);
			} catch (Exception e) {
				// unable to add this listener, just continue with the next.
				e.printStackTrace();
			}
		}
		return listenerList;
	}

}
