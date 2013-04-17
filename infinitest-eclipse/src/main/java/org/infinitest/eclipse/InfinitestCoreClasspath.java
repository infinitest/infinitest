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

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class InfinitestCoreClasspath {
	private static File jarFile;

	public static File getCoreJarLocation(InfinitestPlugin plugin) {
		if ((jarFile == null) || !jarFile.exists()) {
			File tempDir = new File(System.getProperty("java.io.tmpdir"));
			jarFile = getNonConflictingJarFile(tempDir);
			if (!jarFile.getParentFile().exists()) {
				log(SEVERE, "No parent directory for " + jarFile);
				throw new IllegalStateException("No parent directory for " + jarFile);
			}
			writeJar(jarFile, plugin);
		}
		return jarFile;
	}

	private static File getNonConflictingJarFile(File tempDir) {
		File proposedFile = new File(tempDir.getAbsolutePath() + "/infinitest.jar");
		while (proposedFile.exists()) {
			proposedFile = new File(tempDir.getAbsolutePath() + "/infinitest" + randInt() + ".jar");
		}
		proposedFile.deleteOnExit();
		return proposedFile;
	}

	private static int randInt() {
		return (int) (Math.random() * Integer.MAX_VALUE);
	}

	private static void writeJar(File coreJarLocation, InfinitestPlugin plugin) {
		// Only for tests ->
		if (plugin != null)
		// <-
		{
			Enumeration<?> e = plugin.getPluginBundle().findEntries("", "*infinitest-runner*.jar", true);

			if (e == null) {
				log(SEVERE, "Error creating testrunner classpath. Cannot find infinitest core bundle");
			} else {
				while (e.hasMoreElements()) {
					URL resource = (URL) e.nextElement();
					try {
						InputStream in = resource.openStream();
						copyFile(in, coreJarLocation);
					} catch (IOException e1) {
						log(SEVERE, "Error creating testrunner classpath. Could not write to " + coreJarLocation);
						throw new RuntimeException(e1);
					}
				}
			}
		}
	}

	private static void copyFile(InputStream fis, File out) throws IOException {
		if (!out.exists()) {
			out.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(out);
		byte[] buf = new byte[1024];
		int i;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
	}
}
