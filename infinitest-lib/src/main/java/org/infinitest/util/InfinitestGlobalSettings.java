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
package org.infinitest.util;

import static java.util.logging.Level.*;

import java.util.logging.*;

/**
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 */
public class InfinitestGlobalSettings {
	private static Level logLevel = Level.INFO;
	private static long slowTestTimeLimit = 500;

	public static void resetToDefaults() {
		setLogLevel(INFO);
		setSlowTestTimeLimit(500);
	}

	public static Level getLogLevel() {
		return logLevel;
	}

	public static void setLogLevel(Level level) {
		logLevel = level;
	}

	public static void setSlowTestTimeLimit(long timeLimit) {
		slowTestTimeLimit = timeLimit;
	}

	public static long getSlowTestTimeLimit() {
		return slowTestTimeLimit;
	}
}
