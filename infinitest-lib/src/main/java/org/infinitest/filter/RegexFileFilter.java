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
package org.infinitest.filter;

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;

import com.google.common.io.*;

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

	@Override
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
    Closer closer = Closer.create();
    try {
      BufferedReader reader = closer.register(new BufferedReader(new FileReader(file)));
      String line;
      do {
        line = reader.readLine();
        addFilter(line);
      } while (line != null);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    }
	}
}
