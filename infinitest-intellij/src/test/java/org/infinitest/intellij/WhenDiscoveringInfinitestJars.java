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
package org.infinitest.intellij;

import static com.google.common.base.Charsets.UTF_8;
import static org.apache.commons.lang.StringUtils.substringBetween;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.google.common.io.Files;

class WhenDiscoveringInfinitestJars {
  private static final String MAVEN_VERSION = mavenVersion();
  private static final String INFINITEST_RUNNER_JAR = "infinitest-runner-" + MAVEN_VERSION + ".jar";

  @Test
  void shouldDetermineFileNamesFromEmbeddedPom() {
    InfinitestJarsLocator locator = new InfinitestJarsLocator();

    assertThat(locator.findInfinitestRunnerJarName()).isEqualTo(INFINITEST_RUNNER_JAR);
  }

  @Test
  void shouldDetermineInfinitestVersionFromEmbeddedPom() {
    InfinitestJarsLocator locator = new InfinitestJarsLocator();

    assertThat(locator.findInfinitestVersion()).isEqualTo(MAVEN_VERSION);
  }

  private static String mavenVersion() {
    try {
      String pomXml = Files.toString(new File("pom.xml"), UTF_8);
      return substringBetween(pomXml, "<version>", "</version>");
    } catch (IOException e) {
      throw new RuntimeException("Unable to read maven version", e);
    }
  }
}
