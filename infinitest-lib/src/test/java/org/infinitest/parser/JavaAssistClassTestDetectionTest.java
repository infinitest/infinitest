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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.DisabledClassJUnit5TestCase;
import com.fakeco.fakeproduct.DisabledTestJUnit5TestCase;
import com.fakeco.fakeproduct.FakeProduct;
import com.fakeco.fakeproduct.IgnoredClassJUnit4TestCase;
import com.fakeco.fakeproduct.IgnoredTestJUnit4TestCase;
import com.fakeco.fakeproduct.JUnit3TestThatInherits;
import com.fakeco.fakeproduct.JUnit4TestThatInherits;
import com.fakeco.fakeproduct.ParameterizedTest;
import com.fakeco.fakeproduct.TestJUnit4TestCase;
import com.fakeco.fakeproduct.TestJUnit5TestCase;
import com.fakeco.fakeproduct.TestJunit3TestCase;
import com.fakeco.fakeproduct.TestNGFakeProductTest;
import com.fakeco.fakeproduct.TestNGWithClassLevelOnlyTestAnnotationFakeTest;
import com.fakeco.fakeproduct.TestThatInherits;
import com.fakeco.fakeproduct.TestWithACustomRunner;

import javassist.NotFoundException;

class JavaAssistClassTestDetectionTest {
	private ClassPoolForFakeClassesTestUtil classPoolUtil;

	@BeforeEach
	void inContext() throws NotFoundException {
		classPoolUtil = new ClassPoolForFakeClassesTestUtil();
	}

	@Test
	void shouldDetectTestsInClass() {
		assertThat(classPoolUtil.getClass(TestJunit3TestCase.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestJUnit4TestCase.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestJUnit5TestCase.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestThatInherits.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(JUnit3TestThatInherits.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(JUnit4TestThatInherits.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestWithACustomRunner.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(ParameterizedTest.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestNGFakeProductTest.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestNGWithClassLevelOnlyTestAnnotationFakeTest.class).isATest()).isTrue();
		
		// Ignored/disabled tests are still considered as tests, we leave it to the test engine to decide if a test much run
		// We cannot always evaluate conditions (for instance @DisabledForJreRange)t
		assertThat(classPoolUtil.getClass(IgnoredClassJUnit4TestCase.class).isATest()).isFalse();
		assertThat(classPoolUtil.getClass(IgnoredTestJUnit4TestCase.class).isATest()).isFalse();
		assertThat(classPoolUtil.getClass(DisabledClassJUnit5TestCase.class).isATest()).isFalse();
		assertThat(classPoolUtil.getClass(DisabledTestJUnit5TestCase.class).isATest()).isFalse();
	}

	@Test
	void shouldNotDetectTestsInNotTestClass() throws NotFoundException {
		assertThat(classPoolUtil.getClass(FakeProduct.class).isATest()).isFalse();
	}
}
