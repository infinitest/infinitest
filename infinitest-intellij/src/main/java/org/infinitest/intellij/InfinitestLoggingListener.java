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

import static java.lang.String.*;

import java.util.logging.*;

import org.infinitest.intellij.plugin.swingui.*;
import org.infinitest.util.*;

public class InfinitestLoggingListener implements LoggingListener {
	private final InfinitestView view;

	public InfinitestLoggingListener(InfinitestView view) {
		this.view = view;
	}

	public void logError(String message, Throwable throwable) {
		view.writeError(message);
	}

	public void logMessage(Level level, String message) {
		view.writeLogMessage(leftAlign(level) + " " + message);
	}

	private String leftAlign(Level info) {
		return format("%-10s", info.getName());
	}
}
