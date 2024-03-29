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

import java.io.File;
import java.util.List;

import org.infinitest.environment.RuntimeEnvironment;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;

public interface ModuleSettings {
	void writeToLogger(Logger log);

	String getName();

	RuntimeEnvironment getRuntimeEnvironment();
	
	/**
	 * @return The filter {@link File} for this {@link Module}, this should be either the filter file at
	 * the root of the module folder or, if there isn't a file there the project root, or <code>null</code>
	 */
	File getFilterFile();

	
	/**
	 * Builds the list of possible locations for the filter and args files. In IntelliJ they can be either in
	 * the module or in the project root. The list is sorted by priority (module root first).
	 * @return A list consisting of the module working directory and (if there's one) the project base path
	 */
	List<File> getRootDirectories();
}
