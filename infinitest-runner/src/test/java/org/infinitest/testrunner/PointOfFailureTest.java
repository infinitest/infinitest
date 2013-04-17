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
package org.infinitest.testrunner;

import static org.junit.Assert.*;

import org.infinitest.util.*;
import org.junit.*;

public class PointOfFailureTest extends EqualityTestSupport {
	@Test
	public void shouldBeEqualIfExactMatch() {
		PointOfFailure first = new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "");
		PointOfFailure second = new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "");

		assertEquals(first.hashCode(), second.hashCode());
		assertEquals(first, second);
	}

	@Test
	public void shouldNotBeEqualIfAnyFieldDiffers() {
		PointOfFailure pointOfFailure = new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "");

		PointOfFailure differentTestClass = new PointOfFailure("SomeOtherTest", 1, NullPointerException.class.getName(), "");
		assertFalse(pointOfFailure.equals(differentTestClass));

		PointOfFailure differentLineNumber = new PointOfFailure("SomeTest", 2, NullPointerException.class.getName(), "");
		assertFalse(pointOfFailure.equals(differentLineNumber));

		PointOfFailure differentErrorClass = new PointOfFailure("SomeTest", 1, AssertionError.class.getName(), "");
		assertFalse(pointOfFailure.equals(differentErrorClass));

		PointOfFailure differentMessage = new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "foo");
		assertFalse(pointOfFailure.equals(differentMessage));
	}

	@Test
	public void shouldProduceRepresentativeToString() {
		PointOfFailure pointOfFailure = new PointOfFailure("org.infinitest.SomeTest", 1, NullPointerException.class.getSimpleName(), "message");

		String expected = "org.infinitest.SomeTest:1 - NullPointerException(message)";
		assertEquals(expected, pointOfFailure.toString());
	}

	@Test
	public void shouldProvideLineNumberAndClassName() {
		String className = "org.infinitest.SomeTest";
		PointOfFailure pointOfFailure = new PointOfFailure(className, 1, "", "");
		assertEquals(1, pointOfFailure.getLineNumber());
		assertEquals(className, pointOfFailure.getClassName());
	}

	@Test
	public void shouldProduceRepresentativeToStringWithoutMessage() {
		PointOfFailure pointOfFailure = new PointOfFailure("org.infinitest.SomeTest", 1, NullPointerException.class.getSimpleName(), null);

		String expected = "org.infinitest.SomeTest:1 - NullPointerException";
		assertEquals(expected, pointOfFailure.toString());
	}

	@Override
	protected Object createEqualInstance() {
		return new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), null);
	}

	@Override
	protected Object createUnequalInstance() {
		return new PointOfFailure("SomeTest", 1, RuntimeException.class.getName(), null);
	}
}
