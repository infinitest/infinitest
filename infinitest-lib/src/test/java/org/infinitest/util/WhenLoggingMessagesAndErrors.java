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
package org.infinitest.util;

import static com.google.common.collect.Maps.newHashMap;
import static org.infinitest.util.InfinitestGlobalSettings.getLogLevel;
import static org.infinitest.util.InfinitestGlobalSettings.setLogLevel;
import static org.infinitest.util.InfinitestUtils.addLoggingListener;
import static org.infinitest.util.InfinitestUtils.log;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenLoggingMessagesAndErrors implements LoggingListener {
	private Map<String, Throwable> errors;
	private Map<String, Level> messages;
	private Level oldLevel;

	@BeforeEach
	void inContext() {
		errors = newHashMap();
		messages = newHashMap();
		addLoggingListener(this);
		oldLevel = getLogLevel();
	}

	@AfterEach
	void cleanup() {
		setLogLevel(oldLevel);
	}

	@Test
	void shouldFireErrorEventToAllowForAlternateLoggingMethods() {
		String message = "an error occured";
		RuntimeException error = new RuntimeException();
		log(message, error);
		assertEquals(error, errors.get(message));
	}

	@Test
	void shouldFireErrorEventWithoutThrowable() {
		String message = "an error occured";
		log(Level.SEVERE, message);
		assertEquals(Level.SEVERE, messages.get(message));
	}

	@Test
	void shouldFireInfoEvent() {
		String message = "an error occured";
		log(message);
		assertEquals(Level.INFO, messages.get(message));
	}

	@Test
	void canControlLogLevel() {
		setLogLevel(Level.OFF);
		String message = "an error occured";
		log(Level.SEVERE, message);
		assertNull(messages.get(message));
	}

	@Override
	public void logError(String message, Throwable throwable) {
		errors.put(message, throwable);
	}

	@Override
	public void logMessage(Level level, String logMsg) {
		messages.put(logMsg, level);
	}
}
