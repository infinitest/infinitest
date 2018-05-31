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

import java.io.*;

import javassist.*;

import org.infinitest.*;
import org.infinitest.environment.ClasspathProvider;

/**
 * @author Ben Rady
 */
class JavaClassBuilder {
	private final JavaAssistClassParser parser;

	JavaClassBuilder(ClasspathProvider classpath) {
		this(new JavaAssistClassParser(classpath.getRunnerFullClassPath()));
	}

	public JavaClassBuilder(JavaAssistClassParser parser) {
		this.parser = parser;
	}

	public void clear() {
		parser.clear();
	}

	public JavaClass getClass(String classname) {
		try {
			return parser.getClass(classname);
		} catch (RuntimeException e) {
			// Can occur when a cached class disappears from the file system
			rethrowIfSerious(e);
			return new UnparsableClass(classname);
		} catch (MissingClassException e) {
			return new UnparsableClass(classname);
		}
	}

	public String classFileChanged(File file) {
		try {
			return parser.classFileChanged(file);
		} catch (RuntimeException e) {
			// If the class goes missing after we read it in but before we
			// process it,
			// we might get an exception that looks like this
			rethrowIfSerious(e);
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	private void rethrowIfSerious(RuntimeException e) {
		if (!(e.getCause() instanceof NotFoundException)) {
			throw e;
		}
	}
}
