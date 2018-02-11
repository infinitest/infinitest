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

import static org.assertj.core.api.Assertions.*;

import javassist.*;

import org.junit.*;

import com.fakeco.fakeproduct.*;

public class JavaAssistClassTestDetectionTest {
	private ClassPoolForFakeClassesTestUtil classPoolUtil;

	@Before
	public void inContext() throws NotFoundException {
		classPoolUtil = new ClassPoolForFakeClassesTestUtil();
	}

	@Test
	public void shouldDetectTestsInClass() {
		assertThat(classPoolUtil.getClass(TestJUnit4TestCase.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestJUnit5TestCase.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestThatInherits.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(JUnit4TestThatInherits.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestWithACustomRunner.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(ParameterizedTest.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestNGFakeProductTest.class).isATest()).isTrue();
		assertThat(classPoolUtil.getClass(TestNGWithClassLevelOnlyTestAnnotationFakeTest.class).isATest()).isTrue();
	}

	@Test
	public void shouldNotDetectTestsInNotTestClass() throws NotFoundException {
		assertThat(classPoolUtil.getClass(FakeProduct.class).isATest()).isFalse();
	}
}
