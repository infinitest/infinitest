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
package org.infinitest.filter;

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;

/**
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 */
public class RegexFileFilter extends ClassNameFilter implements TestFilter {
	private File file;

	public RegexFileFilter(File filterFile) {
		file = filterFile;
		if (!file.exists()) {
			log(INFO, "Filter file " + file + " does not exist.");
		}

		updateFilterList();
	}

	public RegexFileFilter() {
		super();
	}

	public void updateFilterList() {
		if (file == null) {
			return;
		}

		clearFilters();
		if (file.exists()) {
			tryToReadFilterFile();
		}
	}

	private void tryToReadFilterFile() {
		try {
			readFilterFile();
		} catch (IOException e) {
			throw new RuntimeException("Something horrible happened to the filter file", e);
		}
	}

	private void readFilterFile() throws IOException {
		FileReader fileReader = new FileReader(file);
		try {
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			do {
				line = reader.readLine();
				addFilter(line);
			} while (line != null);
		} finally {
			fileReader.close();
		}
	}
}
