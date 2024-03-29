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
package org.infinitest.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.environment.FakeEnvironments.fakeBuildPaths;
import static org.infinitest.util.InfinitestTestUtils.getFileForClass;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.infinitest.StandaloneClasspath;
import org.infinitest.changedetect.ChangeDetector;
import org.infinitest.changedetect.FileChangeDetector;
import org.infinitest.environment.ClasspathProvider;
import org.infinitest.util.InfinitestTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fakeco.fakeproduct.FakeProduct;
import com.fakeco.fakeproduct.TestFakeProduct;

class WhenLookingForChangedFiles {
	private File altClassDir;
	private ChangeDetector detector;
	private ClasspathProvider classpath;

	@TempDir
	private File temporaryFolder;

	@BeforeEach
	void inContext() throws IOException {
		altClassDir = new File(temporaryFolder, "altClasses");
		altClassDir.mkdir();
		
		List<File> buildPaths = new ArrayList<File>();
		buildPaths.addAll(fakeBuildPaths());
		buildPaths.add(altClassDir);
		classpath = new StandaloneClasspath(buildPaths);
		detector = new FileChangeDetector();
		detector.setClasspathProvider(classpath);
	}

	@Test
	void shouldLookInClasspathForFiles() {
		ClasspathProvider mockClasspath = mock(ClasspathProvider.class);

		new FileChangeDetector().setClasspathProvider(mockClasspath);

		verify(mockClasspath).classDirectoriesInClasspath();
	}

	@Test
	void shouldHandleStrangeClasspaths() throws Exception {
		ChangeDetector changeDetector = new FileChangeDetector();
		changeDetector.setClasspathProvider(new StandaloneClasspath(new ArrayList<File>(), ""));
		assertTrue(changeDetector.findChangedFiles().isEmpty());
	}

	@Test
	void shouldFindChangedFiles() throws IOException {
		Set<File> files = detector.findChangedFiles();

		assertThat(files)
		.contains(getFileForClass(TestFakeProduct.class))
		.contains(getFileForClass(FakeProduct.class));
		assertThat(detector.findChangedFiles()).isEmpty();
	}

	@Test
	void canLookInMultipleClassDirectories() throws Exception {
		File newFile = createFileForClass(TestFakeProduct.class);
		File thisFile = InfinitestTestUtils.getFileForClass(getClass());
		Set<File> changedFiles = detector.findChangedFiles();

		assertThat(changedFiles).contains(newFile, thisFile);
	}

	@Test
	void shouldFindRemovedFiles() throws Exception {
		File newFile = createFileForClass(TestFakeProduct.class);
		assertThat(detector.findChangedFiles()).contains(newFile);

		newFile.delete();

		assertTrue(detector.filesWereRemoved());
		assertThat(detector.findChangedFiles()).doesNotContain(newFile);
	}

	@Test
	void shouldDetectChangedFilesByTimeStamp() throws Exception {
		BasicFileAttributes directoryAttributes = mock(BasicFileAttributes.class);
		BasicFileAttributes classFileAttributes = mock(BasicFileAttributes.class);

		when(directoryAttributes.isDirectory()).thenReturn(true);
		when(classFileAttributes.lastModifiedTime()).thenReturn(FileTime.fromMillis(1000L));

		detector = new FileChangeDetector() {
			@Override
			protected BasicFileAttributes readFileAttributes(Path path) throws IOException {
				if (Files.isDirectory(path)) {
					return directoryAttributes;
				} else {
					return classFileAttributes;
				}
			}
		};
		
		detector.setClasspathProvider(classpath);
	    assertThat(detector.findChangedFiles()).as("Should have found changed files on first run").isNotEmpty();
	    assertThat(detector.findChangedFiles()).as("Timestamp is unchanged").isEmpty();

		// Update the timestamp of the file from 1000 to 1100
		when(classFileAttributes.lastModifiedTime()).thenReturn(FileTime.fromMillis(1100L));

	    assertThat(detector.findChangedFiles()).as("Timestamp changed").isNotEmpty();
	}

	private File createFileForClass(Class<TestFakeProduct> clazz) throws IOException {
		File destFile = InfinitestTestUtils.getFileForClass(altClassDir, clazz.getName());
		assertTrue(destFile.getParentFile().mkdirs());
		assertTrue(destFile.createNewFile());
		return destFile;
	}
}
