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

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static org.infinitest.util.InfinitestUtils.log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Supplier;

import org.infinitest.util.InfinitestUtils;
import org.osgi.framework.Bundle;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;
import com.google.common.io.Resources;

@Component
public class EclipsePluginInfinitestJarsLocator implements InfinitestJarsLocator {

	private static class BundleJarExtractor {
		private final Supplier<Bundle> bundleSupplier;
		private final String extractPattern;
		private final String jarPrefix;

		File extractedJarFile;

		BundleJarExtractor(Supplier<Bundle> bundleSupplier, String jarPrefix, String extractedJarPattern) {
			this.bundleSupplier = bundleSupplier;
			this.jarPrefix = jarPrefix;
			this.extractPattern = extractedJarPattern;
		}

		File getExtractedJarFile() {
			if ((extractedJarFile != null) && extractedJarFile.exists()) {
				return extractedJarFile;
			}

			extractedJarFile = createJar();
			return extractedJarFile;
		}

		private File createJar() {
			try {
				File file = InfinitestUtils.createTempFile(jarPrefix, ".jar");
				file.deleteOnExit();

				extractJarFromBundle(file);
				return file;
			} catch (IOException e) {
				String message = "Error extracting temporary jar from bundle. Could not create temp file with "
						+ jarPrefix + " prefix.";
				log(SEVERE, message);
				throw new RuntimeException(message, e);
			}
		}

		private void extractJarFromBundle(File jarLocation) throws IOException {
			final List<URL> coreBundleUrls = bundleUrlsForPattern();
			if (coreBundleUrls.isEmpty()) {
				String message = "Error creating " + jarLocation + ". No bundle entries matching" + extractPattern + " in bundle.";
				log(SEVERE, message);
				throw new IllegalStateException(message);
			} 
			URL firstUrl = coreBundleUrls.get(0);
			if (coreBundleUrls.size() > 1) {
				String message = "Found multiple bundle entries matching" + extractPattern + " in bundle. Using first url: " + firstUrl;
				log(WARNING, message);				
			}

			Resources.asByteSource(firstUrl).copyTo(Files.asByteSink(jarLocation));
		}

		@SuppressWarnings("unchecked")
		private List<URL> bundleUrlsForPattern() {
			Bundle pluginBundle = bundleSupplier.get();
			@SuppressWarnings("rawtypes")
			Enumeration entries = pluginBundle.findEntries("", extractPattern, true);
			return (List<URL>) Collections.list(entries);
		}

	}

	static Bundle getPluginInstanceBundle() {
		InfinitestPlugin instance = InfinitestPlugin.getInstance();
		if (instance == null) {
			throw new IllegalStateException("InfinitestPlugin not yet initialized!!");
		}
		return instance.getBundle();
	}

	private final BundleJarExtractor runnerJarExtractor;
	private final BundleJarExtractor classLoaderJarExtractor;

	EclipsePluginInfinitestJarsLocator() {
		this(EclipsePluginInfinitestJarsLocator::getPluginInstanceBundle);
	}

	EclipsePluginInfinitestJarsLocator(Supplier<Bundle> bundleSupplier) {
		this.runnerJarExtractor = new BundleJarExtractor(bundleSupplier, "infinitest-runner",
				"*infinitest-runner*.jar");
		this.classLoaderJarExtractor = new BundleJarExtractor(bundleSupplier, "infinitest-classloader",
				"*infinitest-classloader*.jar");
	}

	@Override
	public String getInfinitestRunnerClassPath() {
		return getRunnerJarFile().getAbsolutePath();
	}

	@Override
	public String getInfinitestClassLoaderClassPath() {
		return getClassLoaderJarFile().getAbsolutePath();
	}

	File getRunnerJarFile() {
		return runnerJarExtractor.getExtractedJarFile();
	}

	private File getClassLoaderJarFile() {
		return classLoaderJarExtractor.getExtractedJarFile();
	}

}
