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
package org.infinitest.util;

import static com.google.common.collect.Maps.*;
import static java.lang.System.*;
import static java.util.Arrays.*;

import java.io.*;
import java.util.*;

import javax.swing.*;

import org.infinitest.*;
import org.infinitest.filter.*;

import com.google.common.collect.*;

public class InfinitestPerformanceHarness {
	private static final Multimap<String, Long> perfRecords = ArrayListMultimap.create();
	private static final Map<String, Long> lastStart = newHashMap();

	public static void main(String[] args) {
		System.out.println("Starting");
		File workingDir = new File(".");
		List<File> outputDirs = asList(new File("target/classes", "target/test-classes"));
		String classpath = System.getProperty("java.class.path");
		File javaHome = new File(System.getProperty("java.home"));
		RuntimeEnvironment environment = new RuntimeEnvironment(outputDirs, workingDir, classpath, javaHome);
		InfinitestCoreBuilder builder = new InfinitestCoreBuilder(environment, new ControlledEventQueue());
		builder.setFilter(new TestFilter() {
			public boolean match(String className) {
				return true;
			}

			public void updateFilterList() {
				// nothing to do here
			}
		});
		InfinitestCore core = builder.createCore();
		core.update();
		JOptionPane.showMessageDialog(null, "Start Profiler");
		System.out.println("Updating");
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			core.update();
			core.reload();
			System.out.println(i);
		}
		System.out.println("Finished in " + (System.currentTimeMillis() - start));

		// JOptSimple ~16 sec for 25 updates

		printResults();
		JOptionPane.showMessageDialog(null, "Stop Profiler");
	}

	public static void start(String string) {
		lastStart.put(string, currentTimeMillis());
	}

	public static void stop(String string) {
		perfRecords.put(string, System.currentTimeMillis() - lastStart.get(string));
	}

	private static void printResults() {
		for (String key : perfRecords.keySet()) {
			long totalTime = 0;
			for (Long timestamp : perfRecords.get(key)) {
				totalTime += timestamp;
			}
			System.err.println(key + " : " + totalTime + "ms");
		}
	}

}
