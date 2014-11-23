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

import java.util.*;

import org.testng.*;

/** Provides testNG-settings. Just a data-provider, no logic here. */
public class TestNGConfiguration {
	private String excludedGroups;
	private String groups;
	private List<Object> listeners;

	public void setExcludedGroups(String excludedGroups) {
		this.excludedGroups = excludedGroups;
	}

	public String getExcludedGroups() {
		return excludedGroups;
	}

	public void setGroups(String groupList) {
		groups = groupList;
	}

	public String getGroups() {
		return groups;
	}

	public List<Object> getListeners() {
		return listeners;
	}

	public void setListeners(List<Object> listenerList) {
		listeners = listenerList;
	}

	public void configure(TestNG core) {
		core.setExcludedGroups(excludedGroups);
		core.setGroups(groups);

		if (listeners != null) {
			for (Object listener : listeners) {
				core.addListener(listener);
			}
		}
	}
}
