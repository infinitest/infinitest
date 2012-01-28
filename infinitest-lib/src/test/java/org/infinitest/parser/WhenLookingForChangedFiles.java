/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.parser;

import static com.google.common.io.Files.*;
import static java.util.Collections.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import org.infinitest.*;
import org.infinitest.changedetect.*;
import org.infinitest.util.*;
import org.junit.*;

import com.fakeco.fakeproduct.*;

public class WhenLookingForChangedFiles {
	private File altClassDir;
	private ChangeDetector detector;
	protected long timestamp;
	private ClasspathProvider classpath;

	@Before
	public void inContext() {
		altClassDir = new File("altClasses");

		altClassDir.mkdirs();
		List<File> buildPaths = new ArrayList<File>();
		buildPaths.addAll(fakeBuildPaths());
		buildPaths.add(altClassDir);
		classpath = new StandaloneClasspath(buildPaths);
		detector = new FileChangeDetector();
		detector.setClasspathProvider(classpath);
	}

	@After
	public void cleanup() throws Exception {
		deleteRecursively(altClassDir);
	}

	@Test
	public void shouldLookInClasspathForFiles() {
		ClasspathProvider mockClasspath = mock(ClasspathProvider.class);

		new FileChangeDetector().setClasspathProvider(mockClasspath);

		verify(mockClasspath).classDirectoriesInClasspath();
	}

	@Test
	public void shouldHandleStrangeClasspaths() throws Exception {
		ChangeDetector changeDetector = new FileChangeDetector();
		changeDetector.setClasspathProvider(new StandaloneClasspath(new ArrayList<File>(), ""));
		assertTrue(changeDetector.findChangedFiles().isEmpty());
	}

	@Test
	public void shouldFindChangedFiles() throws IOException {
		Set<File> files = detector.findChangedFiles();
		assertTrue("Should have found changed files on first run", files.size() > 0);
		assertThat("TestFakeProduct should be in changed list", files, hasItem(getFileForClass(TestFakeProduct.class)));
		assertTrue("FakeProduct should be in changed list", files.contains(getFileForClass(FakeProduct.class)));
		assertTrue("Should have no changed files now", detector.findChangedFiles().isEmpty());
	}

	@Test
	public void canLookInMultipleClassDirectories() throws Exception {
		File newFile = createFileForClass(TestFakeProduct.class);
		File thisFile = InfinitestTestUtils.getFileForClass(getClass());
		Set<File> changedFiles = detector.findChangedFiles();
		assertThat(changedFiles, hasItem(newFile));
		assertThat(changedFiles, hasItem(thisFile));
	}

	@Test
	public void shouldFindRemovedFiles() throws Exception {
		File newFile = createFileForClass(TestFakeProduct.class);
		assertThat(detector.findChangedFiles(), hasItem(newFile));

		newFile.delete();

		assertTrue(detector.filesWereRemoved());
		assertThat(detector.findChangedFiles(), not(hasItem(newFile)));
	}

	@Test
	public void shouldDetectChangedFilesByTimeStamp() throws Exception {
		detector = new FileChangeDetector() {
			@Override
			protected long getModificationTimestamp(File classFile) {
				return timestamp;
			}
		};
		detector.setClasspathProvider(classpath);
		assertFalse("Should have found changed files on first run", detector.findChangedFiles().isEmpty());
		assertTrue("Timestamp is unchanged", detector.findChangedFiles().isEmpty());
		timestamp += 100;
		assertFalse("Timestamp changed", detector.findChangedFiles().isEmpty());
	}

	@Test
	public void shouldBeTolerantOfDissapearingDirectories() throws Exception {
		detector = new FileChangeDetector() {
			@Override
			protected File[] childrenOf(File directory) {
				return null;
			}
		};
		detector.setClasspathProvider(classpath);
		assertEquals(emptySet(), detector.findChangedFiles());
	}

	private File createFileForClass(Class<TestFakeProduct> clazz) throws IOException {
		File destFile = InfinitestTestUtils.getFileForClass(altClassDir, clazz.getName());
		assertTrue(destFile.getParentFile().mkdirs());
		assertTrue(destFile.createNewFile());
		return destFile;
	}
}
