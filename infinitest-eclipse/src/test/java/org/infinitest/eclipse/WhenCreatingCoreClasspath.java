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
import static org.infinitest.eclipse.InfinitestCoreClasspath.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.junit.*;
import org.osgi.framework.*;

public class WhenCreatingCoreClasspath {
	private InfinitestPlugin plugin;

	@Before
	public void inContext() {
		Bundle bundle = mock(Bundle.class);
		List<URL> urls = Arrays.asList(getClass().getResource("WhenCreatingCoreClasspath.class"));
		when(bundle.findEntries("", "*infinitest-runner*.jar", true)).thenReturn(enumeration(urls));
		plugin = new InfinitestPlugin();
		plugin.setPluginBundle(bundle);
	}

	@Test
	public void shouldWriteInfinitestCoreOutToTempDirectory() {
		File coreJarLocation = getCoreJarLocation(plugin);
		assertTrue(coreJarLocation.exists());
		assertTrue(coreJarLocation.getAbsolutePath().endsWith(".jar"));
	}

	@Test
	public void shouldRecreateJarIfItIsDeleted() {
		File coreJarLocation = getCoreJarLocation(plugin);
		assertTrue(coreJarLocation.exists());
		assertTrue(coreJarLocation.getAbsolutePath().endsWith(".jar"));
		assertTrue(coreJarLocation.delete());

		coreJarLocation = getCoreJarLocation(plugin);
		assertTrue(coreJarLocation.exists());
	}
}
