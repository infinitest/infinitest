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

import static java.util.Collections.*;
import static org.fest.assertions.Assertions.assertThat;
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
import org.junit.rules.*;

import com.fakeco.fakeproduct.*;

public class WhenLookingForChangedFiles {
  private File altClassDir;
  private ChangeDetector detector;
  protected long timestamp;
  private ClasspathProvider classpath;

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Before
  public void inContext() throws IOException {
    altClassDir = temporaryFolder.newFolder("altClasses");
    List<File> buildPaths = new ArrayList<File>();
    buildPaths.addAll(fakeBuildPaths());
    buildPaths.add(altClassDir);
    classpath = new StandaloneClasspath(buildPaths);
    detector = new FileChangeDetector();
    detector.setClasspathProvider(classpath);
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

    assertThat(files).contains(getFileForClass(TestFakeProduct.class));
    assertThat(files).contains(getFileForClass(FakeProduct.class));
    assertThat(detector.findChangedFiles()).isEmpty();
  }

  @Test
  public void canLookInMultipleClassDirectories() throws Exception {
    File newFile = createFileForClass(TestFakeProduct.class);
    File thisFile = InfinitestTestUtils.getFileForClass(getClass());
    Set<File> changedFiles = detector.findChangedFiles();

    assertThat(changedFiles).contains(newFile, thisFile);
  }

  @Test
  public void shouldFindRemovedFiles() throws Exception {
    File newFile = createFileForClass(TestFakeProduct.class);
    assertThat(detector.findChangedFiles()).contains(newFile);

    newFile.delete();

    assertTrue(detector.filesWereRemoved());
    assertThat(detector.findChangedFiles()).excludes(newFile);
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
