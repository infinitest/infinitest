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
package org.infinitest.eclipse;

import static java.util.Collections.emptyList;
import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Function;

import com.google.common.io.*;

public class InfinitestJarsLocator {

	private static File runnerJarFile;

	private static File classloaderJarFile;

	public static File getRunnerJarLocation(InfinitestPlugin plugin) {
		if ((runnerJarFile != null) && runnerJarFile.exists()) {
			return runnerJarFile;
		}

		runnerJarFile = createJar("infinitest-runner", plugin, "*infinitest-runner*.jar");
		return runnerJarFile;
	}

	public static File getClassLoaderJarLocation(InfinitestPlugin plugin) {
		if ((classloaderJarFile != null) && classloaderJarFile.exists()) {
			return classloaderJarFile;
		}

		classloaderJarFile = createJar("infinitest-classloader", plugin, "*infinitest-classloader*.jar");
		return classloaderJarFile;
	}

	private static File createJar(String baseName, InfinitestPlugin plugin, String filePattern) {
		File file;
		try {
			file = File.createTempFile(baseName, ".jar");
		} catch (IOException e) {
			log(SEVERE, "Error creating runtime jar. Could not create temp dir with " + baseName);
			throw new RuntimeException(e);
		}
		file.deleteOnExit();

		writeJar(file, plugin, filePattern);
		return file;
	}

	private static void writeJar(File coreJarLocation, InfinitestPlugin plugin, String filePattern) {
		final List<URL> coreBundleUrls = coreBundleUrls(plugin, filePattern);
		if (coreBundleUrls.isEmpty()) {
			log(SEVERE, "Error creating " + coreJarLocation + ". Cannot find " + filePattern + " in bundle");
		}
		coreBundleUrls.stream().map(Resources::asByteSource)
				.forEach(byteSource -> writeBytesToJarfile(coreJarLocation, byteSource));
	}

	private static void writeBytesToJarfile(final File coreJarLocation, final ByteSource byteSource) {
		try {
			byteSource.copyTo(Files.asByteSink(coreJarLocation));
		} catch (IOException e1) {
			log(SEVERE, "Error creating runtime jar. Could not write to " + coreJarLocation);
			throw new RuntimeException(e1);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<URL> coreBundleUrls(final InfinitestPlugin plugin, String filePattern) {
		return Optional.ofNullable(plugin).map(InfinitestPlugin::getPluginBundle)
				.map(bundle -> bundle.findEntries("", filePattern, true))
				.map((Function<Enumeration, List>) Collections::list).map(list -> (List<URL>) list).orElse(emptyList());
	}
}
