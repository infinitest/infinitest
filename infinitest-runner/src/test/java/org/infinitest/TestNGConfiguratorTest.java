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

import static org.fest.assertions.Assertions.*;

import java.io.*;
import java.util.*;

import org.junit.*;

public class TestNGConfiguratorTest {
  private static final String LISTENER2 = "org.testng.reporters.JUnitXMLReporter";
  private static final String LISTENER1 = "org.testng.internal.annotations.DefaultAnnotationTransformer";
  private static final String GROUPS = "quick";
  private static final String EXCLUDED = "slow, broken, manual";
  private static final Object LISTENERS = LISTENER1 + ", " + LISTENER2;
  private static final String EXCLUDEDLINE = "## excluded-groups=" + EXCLUDED;
  private static final String GROUPSLINE = "## groups=" + GROUPS;
  private static final String LISTENERLINE = "## listeners=" + LISTENERS;

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
  public void canFilterFromFile() throws IOException {
    File file = file(EXCLUDEDLINE);

    TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

    assertThat(config.getExcludedGroups()).isEqualTo(EXCLUDED);
  }

  @Test
  public void testWithOne() throws IOException {
    File file = file(EXCLUDEDLINE.substring(1));

    TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

    assertThat(config.getExcludedGroups()).isEqualTo(EXCLUDED);
  }

  @Test
  public void testWithThree() throws IOException {
    File file = file("#" + EXCLUDEDLINE);

    TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

    assertThat(config.getExcludedGroups()).isEqualTo(EXCLUDED);
  }

  @Test
  public void testReadingIncludedGroup() throws IOException {
    File file = file(GROUPSLINE);

    TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

    assertThat(config.getGroups()).isEqualTo(GROUPS);
  }

  @Test
  public void testReadingIncludedAndExcludedGroups() throws IOException {
    File file = file(GROUPSLINE, EXCLUDEDLINE);

    TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

    assertThat(config.getGroups()).isEqualTo(GROUPS);
    assertThat(config.getExcludedGroups()).isEqualTo(EXCLUDED);
  }

  @Test
  public void testEmptyGroups() throws IOException {
    File file = file("## excluded-groups= ");

    TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

    assertThat(config.getExcludedGroups()).isNull();
  }

  @Test
  public void testSpacesInGroupsLine() throws IOException {
    String halloGroup = "hallo";
    File file = file("##excluded-groups = " + halloGroup + " ");

    TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

    assertThat(config.getExcludedGroups()).isEqualTo(halloGroup);
  }

  @Test
  public void testEmptyFile() {
    File file = new File("testng.config");

    TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

    assertThat(config.getExcludedGroups()).isNull();
  }

  @Test
  public void testReadingListeners() throws IOException {
    File file = file(LISTENERLINE);

    List<?> listeners = new TestNGConfigurator(file).getConfig().getListeners();
    String listenerName1 = listeners.get(0).getClass().getName();
    String listenerName2 = listeners.get(1).getClass().getName();

    assertThat(listenerName1.equals(LISTENER1) || listenerName1.equals(LISTENER2)).isTrue();
    assertThat(listenerName2.equals(LISTENER1) || listenerName2.equals(LISTENER2)).isTrue();
  }

  private static File file(String... additionalLines) throws IOException {
    File file = File.createTempFile("filter", "conf");
    file.deleteOnExit();
    PrintWriter writer = new PrintWriter(file);
    try {
      writer.println("## TestNG Configuration");
      for (String line : additionalLines) {
        writer.println(line);
      }
      writer.println("#foo.bar");
      writer.println("Some other content");
    } finally {
      writer.close();
    }
    return file;
  }
}
