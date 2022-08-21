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

import static java.util.Collections.enumeration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.infinitest.testrunner.TestRunnerProcess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.Bundle;

@RunWith(MockitoJUnitRunner.class)
public class EclipsePluginInfinitestJarsLocatorTest {
  @Mock
  Bundle bundle;
  

  private EclipsePluginInfinitestJarsLocator jarLocator;
  
  @Before
  public void setup() {
	  jarLocator = new EclipsePluginInfinitestJarsLocator(() -> bundle);
	  List<URL> testRunnerProcessUrls = Arrays.asList(TestRunnerProcess.class.getResource("TestRunnerProcess.class"));
	  when(bundle.findEntries("", "*infinitest-runner*.jar", true)).thenReturn(enumeration(testRunnerProcessUrls), enumeration(testRunnerProcessUrls));
  }

  @Test
  public void shouldWriteInfinitestRunnerJarToTempDirectory() {
    File runnerJarLocation = jarLocator.getRunnerJarFile();

    assertThat(runnerJarLocation).exists();
    assertThat(runnerJarLocation.getAbsolutePath()).endsWith(".jar");
  }
  
  
  @Test
  public void shouldRecreateJarIfItIsDeleted() {
    
    File runnerJarLocation = jarLocator.getRunnerJarFile();

    assertThat(runnerJarLocation.delete()).isTrue();

    File newJarLocation = jarLocator.getRunnerJarFile();
    assertThat(newJarLocation).exists();
    assertThat(newJarLocation).isNotEqualTo(runnerJarLocation);
    assertThat(newJarLocation.getAbsolutePath()).endsWith(".jar");
  }
  
  @Test
  public void shouldCacheFileIfNotDeleted() {
    File runnerJarLocation = jarLocator.getRunnerJarFile();
    long lastModified = runnerJarLocation.lastModified();
    
    File runnerJarLocation2 = jarLocator.getRunnerJarFile();
    long lastModified2 = runnerJarLocation2.lastModified();
    assertThat(runnerJarLocation).isEqualTo(runnerJarLocation2);
	assertThat(lastModified).isEqualTo(lastModified2);
  }

}
