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
package org.infinitest;

import static org.infinitest.InfinitestSettings.IS_ENABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;


class WhenConfiguringInfinitest {
	@Test
	void shouldProvideDefaultValues() {
		InfinitestSettings settings = new InfinitestSettings();
		assertTrue(settings.isInfinitestEnabled());
	}

	@Test
	void canLoadSettingsFromAStream() throws IOException {
		Properties properties = new Properties();
		properties.setProperty(IS_ENABLED, "true");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		properties.store(out, "");
		InfinitestSettings settings = new InfinitestSettings(new ByteArrayInputStream(out.toByteArray()));
		assertTrue(settings.isInfinitestEnabled());
	}

	@Test
	void canUpdateSettingsInPlace() {
		InfinitestSettings settings = new InfinitestSettings();
		settings.setIsInfinitestEnabled(false);
		assertFalse(settings.isInfinitestEnabled());
	}

	@Test
	void canSavePropertiesToAStream() throws IOException {
		InfinitestSettings settings = new InfinitestSettings();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		settings.saveTo(outputStream);
		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(outputStream.toByteArray()));
		assertEquals("true", properties.getProperty(IS_ENABLED));
	}
}
