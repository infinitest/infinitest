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
package org.infinitest.eclipse.resolution;

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.*;

public class WhenFilteringUnnecessaryClassesFromStackTraces {
	private StackTraceFilter filter;

	@Before
	public void inContext() {
		filter = new StackTraceFilter();
	}

	@Test
	public void shouldRemoveInfinitestRunnerClasses() {
		assertEquals(emptyList(), filter.filterStack(newArrayList(element("org.infinitest.runner.Foobar"))));
	}

	@Test
	public void shouldNotRemoveRegularInfinitestClasses() {
		assertFalse(filter.filterStack(newArrayList(element("org.infinitest.Foobar"))).isEmpty());
	}

	@Test
	public void shouldRemoveJUnitClasses() {
		assertThat(filter.filterStack(newArrayList(
				element("org.junit.Foobar"),
				element("junit.framework.Foobar"))))
				.isEmpty();
	}

	@Test
	public void shouldRemoveSunReflectionClasses() {
		assertEquals(emptyList(), filter.filterStack(newArrayList(element("sun.reflect.Foo"), element("java.lang.reflect.Method"))));
	}

	private StackTraceElement element(String classname) {
		return new StackTraceElement(classname, "someMethod", "someFile", 0);
	}
}
