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
package org.infinitest;

import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

public class TestNGConfiguratorTest {
  TestNGConfigurator configurator = new TestNGConfigurator();

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Test
  public void canExcludeNothing() {
    TestNGConfiguration config = new TestNGConfiguration();

    assertThat(config.getExcludedGroups()).isNull();
  }

  @Test
  public void canIncludeNothing() {
    TestNGConfiguration config = new TestNGConfiguration();

    assertThat(config.getGroups()).isNull();
  }

  @Test
  public void canFilter() {
    TestNGConfiguration config = read("## excluded-groups=slow, broken, manual");

    assertThat(config.getExcludedGroups()).isEqualTo("slow, broken, manual");
  }

  @Test
  public void testWithOne() {
    TestNGConfiguration config = read("# excluded-groups=slow, broken, manual");

    assertThat(config.getExcludedGroups()).isEqualTo("slow, broken, manual");
  }

  @Test
  public void testWithThree() {
    TestNGConfiguration config = read("### excluded-groups=slow, broken, manual");

    assertThat(config.getExcludedGroups()).isEqualTo("slow, broken, manual");
  }

  @Test
  public void testReadingIncludedGroup() {
    TestNGConfiguration config = read("## groups=quick");

    assertThat(config.getGroups()).isEqualTo("quick");
  }

  @Test
  public void testReadingIncludedAndExcludedGroups() {
    TestNGConfiguration config = read("## groups=quick", "## excluded-groups=slow, broken, manual");

    assertThat(config.getGroups()).isEqualTo("quick");
    assertThat(config.getExcludedGroups()).isEqualTo("slow, broken, manual");
  }

  @Test
  public void testEmptyGroups() {
    TestNGConfiguration config = read("## excluded-groups= ");

    assertThat(config.getExcludedGroups()).isNull();
  }

  @Test
  public void testSpacesInGroupsLine() {
    TestNGConfiguration config = read("##excluded-groups = hallo ");

    assertThat(config.getExcludedGroups()).isEqualTo("hallo");
  }

  @Test
  public void testEmptyFile() {
    TestNGConfiguration config = read("testng.config");

    assertThat(config.getExcludedGroups()).isNull();
  }

  @Test
  public void testReadingListeners() {
    TestNGConfiguration config = read("## listeners=org.testng.internal.annotations.DefaultAnnotationTransformer, org.testng.reporters.JUnitXMLReporter");

    List<Object> listeners = config.getListeners();

    assertThat(listeners.get(0)).isExactlyInstanceOf(org.testng.internal.annotations.DefaultAnnotationTransformer.class);
    assertThat(listeners.get(1)).isExactlyInstanceOf(org.testng.reporters.JUnitXMLReporter.class);
  }

  private TestNGConfiguration read(String... lines) {
    return configurator.readConfig(file(lines));
  }

  private File file(String... lines) {
    try {
      File file = temp.newFile("infinitest.filters");

      PrintWriter writer = new PrintWriter(file);
      try {
        writer.println("## TestNG Configuration");
        for (String line : lines) {
          writer.println(line);
        }
        writer.println("#foo.bar");
        writer.println("Some other content");
      } finally {
        writer.close();
      }
      return file;
    } catch (IOException e) {
      throw new RuntimeException("Unable to create test file");
    }
  }
}
