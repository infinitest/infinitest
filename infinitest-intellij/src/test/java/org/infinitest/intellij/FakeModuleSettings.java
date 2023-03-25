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

import static java.lang.System.getProperty;
import static java.util.Collections.emptyList;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.infinitest.config.FileBasedInfinitestConfigurationSource;
import org.infinitest.environment.RuntimeEnvironment;

import com.intellij.openapi.diagnostic.Logger;

public class FakeModuleSettings implements ModuleSettings {
	private final String name;

	public FakeModuleSettings(String name) {
		this.name = name;
	}

	@Override
	public void writeToLogger(Logger log) {
		// nothing to do here
	}

	@Override
	public String getName() {
		return name;
	}

	public List<File> listOutputDirectories() {
		return emptyList();
	}

	public String buildClasspathString() {
		return null;
	}

	public File getWorkingDirectory() {
		return new File(".");
	}

	@Override
	public RuntimeEnvironment getRuntimeEnvironment() {
		String systemClasspath = System.getProperty("java.class.path");
		return new RuntimeEnvironment(new File(getProperty("java.home")),
				new File("."),
				systemClasspath, systemClasspath,
				Collections.<File>emptyList(),
				null,
				FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory());
	}
	
	@Override
	public File getFilterFile() {
		return null;
	}
	
	@Override
	public List<File> getRootDirectories() {
		return Collections.emptyList();
	}
}
