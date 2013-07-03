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

import static java.util.Collections.*;
import static org.fest.assertions.Assertions.*;
import static org.infinitest.eclipse.InfinitestCoreClasspath.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.osgi.framework.*;

@RunWith(MockitoJUnitRunner.class)
public class WhenCreatingCoreClasspath {
  @Mock
  Bundle bundle;

  InfinitestPlugin plugin = new InfinitestPlugin();

  @Test
  public void shouldWriteInfinitestCoreOutToTempDirectory() {
    File coreJarLocation = getCoreJarLocation(plugin);

    assertThat(coreJarLocation).exists();
    assertThat(coreJarLocation.getAbsolutePath()).endsWith(".jar");
  }

  @Test
  public void shouldRecreateJarIfItIsDeleted() {
    List<URL> urls = Arrays.asList(getClass().getResource("WhenCreatingCoreClasspath.class"));
    when(bundle.findEntries("", "*infinitest-runner*.jar", true)).thenReturn(enumeration(urls), enumeration(urls));
    plugin.setPluginBundle(bundle);

    File coreJarLocation = getCoreJarLocation(plugin);
    assertThat(coreJarLocation.delete()).isTrue();

    coreJarLocation = getCoreJarLocation(plugin);
    assertThat(coreJarLocation).exists();
    assertThat(coreJarLocation.getAbsolutePath()).endsWith(".jar");
  }
}
