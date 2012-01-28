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

import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import org.junit.*;

public class TestInfinitestUtils {
	@Test
	public void shouldFormatTimeAsHHMMSS() {
		assertEquals("00:00:20", InfinitestUtils.formatTime(20 * 1000));
	}

	@Test
	public void shouldAddLineFeedsToErrorMessagesWithAPath() {
		String actualMessage = getExceptionMessage(new IllegalArgumentException("path:path2:path3"));
		assertEquals("path:\npath2:\npath3", actualMessage);
	}

	@Test
	public void shouldProvideShortClassNameForTest() {
		assertEquals("TestFoo", stripPackageName("com.foobar.TestFoo"));
	}

	@Test
	public void shouldTruncateLongErrorMessages() {
		String actualMessage = getExceptionMessage(new IllegalArgumentException("This is a really long error message that needs to be truncated to a reasonable length."));
		assertEquals("This is a really long error message that needs to ...", actualMessage);
	}

	@Test
	public void shouldUseClassNameWhenMessageIsNull() {
		String actualMessage = getExceptionMessage(new NullPointerException());
		assertEquals(NullPointerException.class.getName(), actualMessage);
	}

	@Test
	public void shouldAlwaysUseForwardSlashForResourcePath() {
		assertFalse(getResourceName(TestInfinitestUtils.class.getName()).contains("\\"));
	}
}
