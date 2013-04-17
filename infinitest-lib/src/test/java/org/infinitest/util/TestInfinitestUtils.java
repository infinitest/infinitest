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
