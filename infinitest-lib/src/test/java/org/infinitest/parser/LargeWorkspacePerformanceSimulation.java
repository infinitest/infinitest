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

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.environment.FakeEnvironments.fakeClasspath;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.infinitest.changedetect.FileChangeDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LargeWorkspacePerformanceSimulation {
  private static final int PROJECT_COUNT = 25;
  private static final int UPDATE_COUNT = 10;
  private boolean showOutput;
  private List<File> files;
  private List<ClassFileIndex> indexes;

  @BeforeEach
  void inContext() throws IOException {
    FileChangeDetector detector = new FileChangeDetector();
    detector.setClasspathProvider(fakeClasspath());
    indexes = newArrayList();
    files = newArrayList(detector.findChangedFiles());
  }

  public static void main(String[] args) throws IOException {
    // At last count, we could do this in 22 seconds on a 2.4ghz MacBookPro.
    // Although, it looks like when you run it in the test it runs a little
    // slower
    LargeWorkspacePerformanceSimulation testHarness = new LargeWorkspacePerformanceSimulation();
    testHarness.showOutput = true;
    testHarness.inContext();
    System.out.println("File Count: " + testHarness.files.size());
    System.out.println("Max Memory " + humanReadable(Runtime.getRuntime().maxMemory()));

    long timestamp = System.currentTimeMillis();
    System.out.println("Creating Projects...");
    testHarness.createProjects();
    System.out.println("Created in " + (System.currentTimeMillis() - timestamp) + "ms");
    System.out.println("Updating Projects...");

    timestamp = System.currentTimeMillis();
    testHarness.updateProjects();
    System.out.println();
    long time = System.currentTimeMillis() - timestamp;
    System.out.println("Updated " + PROJECT_COUNT + " projects " + UPDATE_COUNT + " times in " + time + "ms");
  }

  @Test
  void canScaleTo25ProjectsOfAtLeast250ClassesEach() {
    assertThat(files.size()).isGreaterThan(250);

    long timestamp = System.currentTimeMillis();
    createProjects();
    // We should be able to do this in 35 seconds on a 2.4ghz MBP. 60 sec is
    // just for test
    // consistency
    assertThat(System.currentTimeMillis() - timestamp).isLessThan(300000L);

    timestamp = System.currentTimeMillis();
    updateProjects();
    // We should be able to do this in 3 seconds on a 2.4ghz MBP. 10 sec is
    // just for test
    // consistency
    assertThat(System.currentTimeMillis() - timestamp).isLessThan(10000L);
  }

  private void updateProjects() {
    for (int updateIndex = 1; updateIndex <= UPDATE_COUNT; updateIndex++) {
      println("Pass " + updateIndex);
      simulateUpdates(files, indexes);
      println("");
    }
  }

  private void createProjects() {
    for (int projectIndex = 0; projectIndex < PROJECT_COUNT; projectIndex++) {
      print(".");
      indexes.add(simulateNewProject());
    }
  }

  private ClassFileIndex simulateNewProject() {
    ClassFileIndex index = new ClassFileIndex(fakeClasspath());
    index.findClasses(files);
    return index;
  }

  private void simulateUpdates(List<File> files, List<ClassFileIndex> indexes) {
    int filesChanges = 0;
    for (ClassFileIndex each : indexes) {
      Set<JavaClass> classes = each.findClasses(files.subList(0, filesChanges++ % 10));
      each.findChangedParents(classes);
      print(".");
    }
  }

  private void print(String string) {
    if (showOutput) {
      System.out.print(string);
    }
  }

  private void println(String string) {
    if (showOutput) {
      System.out.println(string);
    }
  }

  private static String humanReadable(long memory) {
    return (memory / 1024) + "k";
  }

}
