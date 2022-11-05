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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.AbstractTestFakeProduct;
import com.fakeco.fakeproduct.AllTests;
import com.fakeco.fakeproduct.ClassWithAnInnerTestClass;
import com.fakeco.fakeproduct.JUnit3TestThatInherits;
import com.fakeco.fakeproduct.JUnit4TestThatInherits;
import com.fakeco.fakeproduct.LooksLikeAJUnit3TestButIsnt;
import com.fakeco.fakeproduct.ParameterizedTest;
import com.fakeco.fakeproduct.TestJUnit5TestCase;
import com.fakeco.fakeproduct.TestJunit3TestCase;
import com.fakeco.fakeproduct.TestNGFakeProductTest;
import com.fakeco.fakeproduct.TestThatInheritsACustomRunner;
import com.fakeco.fakeproduct.TestWithACustomRunner;
import com.fakeco.fakeproduct.ValidTestWithUnconventionalConstructor;

import javassist.ClassPool;
import javassist.NotFoundException;

class WhenLookingForTests {
	private ClassPool classPool;

	@BeforeEach
	void inContext() throws NotFoundException {
		classPool = new ClassPool();
		classPool.appendPathList(fakeClasspath().getRunnerFullClassPath());
		classPool.appendSystemPath();
	}

	@Test
	void shouldIgnoreAbstractClasses() {
		assertFalse(classFor(AbstractTestFakeProduct.class).isATest());
	}

	@Test
	void shouldIgnoreInnerClasses() {
		assertFalse(classFor(ClassWithAnInnerTestClass.InnerTest.class).isATest());
	}

	@Test
	void shouldIgnoreTestCasesWithSuiteMethods() {
		assertFalse(classFor(AllTests.class).isATest());
	}

	@Test
	void shouldDetectParameterizedTestsWithAlternateConstructors() {
		assertTrue(classFor(ParameterizedTest.class).isATest());
	}

	@Test
	void shouldDetectClassesThatInheritFromTests() {
		assertTrue(classFor(JUnit4TestThatInherits.class).isATest());
		assertTrue(classFor(JUnit3TestThatInherits.class).isATest());
	}

	@Test
	void canDetectTestsWithCustomRunners() {
		assertTrue(classFor(TestWithACustomRunner.class).isATest());
	}

	@Test
	void shouldDetectClassesThatInheritCustomRunners() {
		assertTrue(classFor(TestThatInheritsACustomRunner.class).isATest());
	}

	@Test
	void shouldDetectTestThatInheritARunnerAndHasUnconventionalConstructor() {
		assertTrue(classFor(ValidTestWithUnconventionalConstructor.class).isATest());
	}

	@Test
	void canDetectJUnit3Tests() {
		assertTrue(classFor(TestJunit3TestCase.class).isATest());
		assertFalse(classFor(LooksLikeAJUnit3TestButIsnt.class).isATest());
	}

	@Test
	void canDetectJUnit5Tests() {
		assertThat(classFor(TestJUnit5TestCase.class).isATest()).as("JUnit5 (Jupiter) annotated test is detected").isTrue();
	}

	@Test
	void canDetectTestNGTests() {
		assertTrue(classFor(TestNGFakeProductTest.class).isATest());
	}

	private JavaAssistClass classFor(Class<?> testClass) {
		try {
			return new JavaAssistClass(classPool.get(testClass.getName()));
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
