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
package org.infinitest.intellij;

import static java.lang.System.*;
import static java.util.Collections.*;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.infinitest.*;

public class FakeModuleSettings implements ModuleSettings {
	private final String name;

	public FakeModuleSettings(String name) {
		this.name = name;
	}

	public void writeToLogger(Logger log) {
		// nothing to do here
	}

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

	public RuntimeEnvironment getRuntimeEnvironment() {
		return new RuntimeEnvironment(Collections.<File> emptyList(), new File("."), "", new File(getProperty("java.home")));
	}
}
