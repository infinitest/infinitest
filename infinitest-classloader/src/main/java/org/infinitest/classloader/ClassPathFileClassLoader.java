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
package org.infinitest.classloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.List;

/**
 * ClassLoader reading classpath from a file to avoid arguments too
 * long errors.
 * It can be registered as "java.system.class.loader".
 * Implementation should not use any non java standard class to avoid strange issues when used as a System ClassLoader
 * 
 * @author sarod
 *
 */
public class ClassPathFileClassLoader extends URLClassLoader {

	public static final String CLASS_PATH_FILE_PROPERTY = "org.infinitest.classloader.classPathFile";

	public ClassPathFileClassLoader(ClassLoader parentLoader) {
		this(parentLoader, readEntriesFromClassPathFile());
	}

	private static List<String> readEntriesFromClassPathFile() {
		String classPathFile = System.getProperty(CLASS_PATH_FILE_PROPERTY);
		try {
			return Files.readAllLines(new File(classPathFile).toPath());
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot read classpathFile: " + classPathFile);
		}

	}

	public ClassPathFileClassLoader(ClassLoader parentLoader, List<String> classPathEntries) {
		super(toUrlEntries(classPathEntries), parentLoader);
	}

	private static URL[] toUrlEntries(List<String> classPathEntries) {
		URL[] urlEntries = new URL[classPathEntries.size()];
		for (int i = 0; i < classPathEntries.size(); i++) {
			String cpEntry = classPathEntries.get(i);
			File file = new File(cpEntry);
			try {
				urlEntries[i] = file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException("Invalid classpath entry: " + cpEntry, e);
			}
		}

		return urlEntries;
	}
}
