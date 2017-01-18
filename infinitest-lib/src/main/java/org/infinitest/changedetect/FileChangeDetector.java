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
package org.infinitest.changedetect;

import static java.lang.Character.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.infinitest.*;
import org.infinitest.util.*;

public class FileChangeDetector implements ChangeDetector {
	private Map<File, Long> timestampIndex;
	private File[] classDirectories;

	public FileChangeDetector() {
		classDirectories = new File[0];
		clear();
	}

	@Override
	public void setClasspathProvider(ClasspathProvider classpath) {
		clear();
		// List<File> classDirs = classpath.classDirectoriesInClasspath();
		List<File> classDirs = Arrays.asList(classpath.getWorkingDirectory());
		classDirectories = classDirs.toArray(new File[classDirs.size()]);
	}

	@Override
	public synchronized Set<File> findChangedFiles() throws IOException {
		return findFiles(classDirectories, false);
	}

	private Set<File> findFiles(File[] classesOrDirectories, boolean isPackage) throws IOException {
		Set<File> changedFiles = new HashSet<File>();
		for (File classFileOrDirectory : classesOrDirectories) {
			InfinitestUtils.log("ClassFileOrDirectory : " + classFileOrDirectory.toString());
			if (classFileOrDirectory.isDirectory() && hasValidName(classFileOrDirectory, isPackage)) {
				findChildren(changedFiles, classFileOrDirectory);
			} else {
				File classFile = classFileOrDirectory;
				Long timestamp = timestampIndex.get(classFile);
				if ((timestamp == null) || (getModificationTimestamp(classFile) != timestamp)) {
					timestampIndex.put(classFile, getModificationTimestamp(classFile));
					changedFiles.add(classFile);
					InfinitestUtils.log(Level.FINEST, "Class file added to changelist " + classFile);
				}
			}
		}
		return changedFiles;
	}

	private void findChildren(Set<File> changedFiles, File classFileOrDirectory) throws IOException {
		File[] children = childrenOf(classFileOrDirectory);
		if (children != null) {
			changedFiles.addAll(findFiles(children, true));
		}
	}

	protected File[] childrenOf(File directory) {
		return directory.listFiles(new ClassFileFilter());
	}

	private boolean hasValidName(File classfileOrDirectory, boolean isPackage) {
		return !isPackage || isJavaIdentifierStart(classfileOrDirectory.getName().charAt(0));
	}

	protected long getModificationTimestamp(File classFile) {
		return classFile.lastModified();
	}

	@Override
	public synchronized void clear() {
		timestampIndex = new HashMap<File, Long>();
	}

	private Set<File> findRemovedFiles() {
		Set<File> removedFiles = new HashSet<File>();
		for (File key : timestampIndex.keySet()) {
			if (!key.exists()) {
				removedFiles.add(key);
			}
		}
		return removedFiles;
	}

	@Override
	public synchronized boolean filesWereRemoved() {
		return !findRemovedFiles().isEmpty();
	}
}
