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

import static java.util.logging.Level.*;
import static org.infinitest.eclipse.InfinitestPlugin.*;

import java.util.logging.*;

import org.eclipse.core.runtime.*;
import org.infinitest.util.*;

public class EclipseLoggingListener implements LoggingListener {
	@Override
	public void logError(String message, Throwable throwable) {
		Status status = new Status(IStatus.ERROR, PLUGIN_ID, message, throwable);
		InfinitestPlugin plugin = InfinitestPlugin.getInstance();
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}

	@Override
	public void logMessage(Level level, String message) {
		Status status = new Status(levelToStatus(level), PLUGIN_ID, message);
		InfinitestPlugin plugin = InfinitestPlugin.getInstance();
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}

	private int levelToStatus(Level level) {
		if (level.equals(SEVERE)) {
			return IStatus.ERROR;
		}
		if (level.equals(WARNING)) {
			return IStatus.WARNING;
		}
		return IStatus.INFO;
	}
}
