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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.infinitest.environment.ClasspathProvider;
import org.infinitest.util.InfinitestUtils;

public class FileChangeDetector implements ChangeDetector {
	private Map<Path, Long> timestampIndex;
	private List<Path> classDirectories;

	public FileChangeDetector() {
		classDirectories = new ArrayList<>();
		timestampIndex = new HashMap<>();
	}

	@Override
	public void setClasspathProvider(ClasspathProvider classpath) {
		clear();
		List<File> classDirs = classpath.classDirectoriesInClasspath();
		classDirectories = classDirs.stream().map(File::toPath).collect(Collectors.toList());
	}

	@Override
	public synchronized Set<File> findChangedFiles() throws IOException {
		Set<Path> changedFiles = new HashSet<Path>();
		
		for (Path root : classDirectories) {
			try (Stream<Path> stream = Files.walk(root, Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)) {
				stream.forEach(path -> processFile(path, changedFiles));
			}
		}
		
		return changedFiles.stream().map(Path::toFile).collect(Collectors.toSet());
	}

	protected void processFile(Path classFile, Set<Path> changedFiles) {
		try {
			BasicFileAttributes attributes = readFileAttributes(classFile);

			if (!attributes.isDirectory() && ClassFileFilter.isClassFile(classFile)) {
				Long timestamp = timestampIndex.get(classFile);
				long lastModifiedTime = attributes.lastModifiedTime().toMillis();

				if ((timestamp == null) || (lastModifiedTime != timestamp)) {
					timestampIndex.put(classFile, lastModifiedTime);
					changedFiles.add(classFile);
					InfinitestUtils.log(Level.FINEST, "Class file added to changelist " + classFile);
				}
			}
		} catch (IOException e) {

		}
	}

	protected BasicFileAttributes readFileAttributes(Path path) throws IOException {
		return Files.readAttributes(path, BasicFileAttributes.class);
	}

	@Override
	public synchronized void clear() {
		timestampIndex.clear();
	}

	@Override
	public synchronized boolean filesWereRemoved() {
		return timestampIndex.keySet().stream().anyMatch(Files::notExists);
	}
}
