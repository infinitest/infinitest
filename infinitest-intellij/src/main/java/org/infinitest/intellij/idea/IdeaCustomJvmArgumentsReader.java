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
package org.infinitest.intellij.idea;

import java.io.File;
import java.util.List;

import org.infinitest.environment.CustomJvmArgumentsReader;
import org.infinitest.environment.FileCustomJvmArgumentReader;
import org.infinitest.intellij.ModuleSettings;

import com.intellij.openapi.module.Module;

/**
 * @author gtoison
 *
 */
public class IdeaCustomJvmArgumentsReader implements CustomJvmArgumentsReader {
	private Module module;
	
	public IdeaCustomJvmArgumentsReader(Module module) {
		this.module = module;
	}
	
	@Override
	public List<String> readCustomArguments() {
		ModuleSettings moduleSettings = module.getService(ModuleSettings.class);
		
		File[] directories = moduleSettings.getRootDirectories().toArray(File[]::new);
		FileCustomJvmArgumentReader reader = new FileCustomJvmArgumentReader(directories);
		
		return reader.readCustomArguments();
	}
}
