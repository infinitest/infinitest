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
package org.infinitest.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.environment.FakeEnvironments.fakeClasspath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.JUnit5Testable;
import com.fakeco.fakeproduct.JUnit5TestableAbstractClass;
import com.fakeco.fakeproduct.JUnit5TestableSubclass;

import javassist.ClassPool;
import javassist.NotFoundException;

class TestableDetectionTest {
	private ClassPool classPool;

	@BeforeEach
	void inContext() throws NotFoundException {
		classPool = new ClassPool();
		classPool.appendPathList(fakeClasspath().getRunnerFullClassPath());
		classPool.appendSystemPath();
	}

	@Test
	void shouldDetectTestable() {
		assertThat(classFor(JUnit5Testable.class).isATest()).isTrue();
	}

	@Test
	void shouldDetectTestableInParentClass() {
		assertThat(classFor(JUnit5TestableSubclass.class).isATest()).isTrue();
	}

	@Test
	void shouldNotDetectAbstractClassAsTestable() {
		assertThat(classFor(JUnit5TestableAbstractClass.class).isATest()).isFalse();
	}

	private JavaAssistClass classFor(Class<?> testClass) {
		try {
			return new JavaAssistClass(classPool.get(testClass.getName()));
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
