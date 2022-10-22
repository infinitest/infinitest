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

import static org.assertj.core.api.Assertions.assertThat;

import org.infinitest.util.EqualityTestSupport;
import org.junit.jupiter.api.Test;

class PointOfFailureTest extends EqualityTestSupport {
	private static final String TEST_CLASS_NAME = "org.infinitest.SomeTest";

	@Test
	void shouldBeEqualIfExactMatch() {
		PointOfFailure first = createPointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "");
		PointOfFailure second = createPointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "");

		assertThat(first).hasSameHashCodeAs(second).isEqualTo(second);
	}

	@Test
	void shouldNotBeEqualIfAnyFieldDiffers() {
		PointOfFailure pointOfFailure = createPointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "");

		PointOfFailure differentTestClass = createPointOfFailure("SomeOtherTest", 1, NullPointerException.class.getName(), "");
		assertThat(pointOfFailure.equals(differentTestClass)).isFalse();

		PointOfFailure differentLineNumber = createPointOfFailure("SomeTest", 2, NullPointerException.class.getName(), "");
		assertThat(pointOfFailure.equals(differentLineNumber)).isFalse();

		PointOfFailure differentErrorClass = createPointOfFailure("SomeTest", 1, AssertionError.class.getName(), "");
		assertThat(pointOfFailure.equals(differentErrorClass)).isFalse();

		PointOfFailure differentMessage = createPointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "foo");
		assertThat(pointOfFailure.equals(differentMessage)).isFalse();
	}

	@Test
	void shouldProduceRepresentativeToString() {
		PointOfFailure pointOfFailure = createPointOfFailure(TEST_CLASS_NAME, 1, NullPointerException.class.getSimpleName(), "message");

		String expected = "org.infinitest.SomeTest:1 - NullPointerException(message)";
		assertThat(pointOfFailure).hasToString(expected);
	}

	@Test
	void shouldProvideLineNumberAndClassName() {
		PointOfFailure pointOfFailure = createSimplePointOfFailure(TEST_CLASS_NAME);
		assertThat(pointOfFailure.getLineNumber()).isEqualTo(1);
		assertThat(pointOfFailure.getClassName()).isEqualTo(TEST_CLASS_NAME);
	}

	@Test
	void shouldRemoveInnerClassesNamesAppendedToClassName() {
		String INNER_CLASS_NAME = "$InnerClass";
		PointOfFailure pointOfFailure1 = createSimplePointOfFailure(TEST_CLASS_NAME + INNER_CLASS_NAME);
		PointOfFailure pointOfFailure2 = createSimplePointOfFailure(TEST_CLASS_NAME + INNER_CLASS_NAME + INNER_CLASS_NAME);

		assertThat(pointOfFailure1.getClassName()).isEqualTo(TEST_CLASS_NAME);
		assertThat(pointOfFailure2.getClassName()).isEqualTo(TEST_CLASS_NAME);
	}

	private PointOfFailure createSimplePointOfFailure(String className) {
		return createPointOfFailure(className, 1, "", "");
	}

	@Test
	void shouldProduceRepresentativeToStringWithoutMessage() {
		PointOfFailure pointOfFailure = createPointOfFailure(TEST_CLASS_NAME, 1, NullPointerException.class.getSimpleName(), null);

		String expected = "org.infinitest.SomeTest:1 - NullPointerException";
		assertThat(pointOfFailure).hasToString(expected);
	}

	private PointOfFailure createPointOfFailure(String className, int lineNumber, String errorClassName, String message) {
		return new PointOfFailure(className, lineNumber, errorClassName, message);
	}

	@Override
	protected Object createEqualInstance() {
		return createPointOfFailure("SomeTest", 1, NullPointerException.class.getName(), null);
	}

	@Override
	protected Object createUnequalInstance() {
		return createPointOfFailure("SomeTest", 1, RuntimeException.class.getName(), null);
	}
}
