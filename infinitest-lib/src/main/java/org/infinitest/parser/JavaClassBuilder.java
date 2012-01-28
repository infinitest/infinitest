/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.parser;

import java.io.*;

import javassist.*;

import org.infinitest.*;

/**
 * @author Ben Rady
 */
class JavaClassBuilder implements ClassBuilder {
	private final ClassParser parser;

	JavaClassBuilder(ClasspathProvider classpath) {
		this(new JavaAssistClassParser(classpath.getCompleteClasspath()));
	}

	public JavaClassBuilder(ClassParser parser) {
		this.parser = parser;
	}

	public JavaClass createClass(String classname) {
		try {
			return parser.getClass(classname);
		}
		// CHECKSTYLE:OFF
		catch (RuntimeException e)
		// CHECKSTYLE:ON
		{
			// Can occur when a cached class disappears from the file system
			rethrowIfSerious(e);
			return new UnparsableClass(classname);
		} catch (MissingClassException e) {
			return new UnparsableClass(classname);
		}
	}

	public JavaClass loadClass(File file) {
		try {
			return parser.parse(file);
		}
		// CHECKSTYLE:OFF
		catch (RuntimeException e)
		// CHECKSTYLE:ON
		{
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

	public void clear() {
		parser.clear();
	}
}
