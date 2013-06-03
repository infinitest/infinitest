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

import static junitparams.JUnitParamsRunner.*;
import static org.junit.Assert.*;

import javassist.*;
import junitparams.*;

import org.junit.*;
import org.junit.runner.*;

import com.fakeco.fakeproduct.*;

@RunWith(JUnitParamsRunner.class)
public class JavaAssistClassTestDetectionTest {

	private ClassPoolForFakeClassesTestUtil classPoolUtil;

	@Before
	public void inContext() throws NotFoundException {
		classPoolUtil = new ClassPoolForFakeClassesTestUtil();
	}

	@Test
	@Parameters
	public void shouldDetectTestsInClass(Class clazz) {
		assertTrue(classPoolUtil.getClass(clazz).isATest());
	}

	@Test
	public void shouldNotDetectTestsInNotTestClass() throws NotFoundException {
		assertFalse(classPoolUtil.getClass(FakeProduct.class).isATest());
	}

	private Object[] parametersForShouldDetectTestsInClass() {
		return $(
				$(TestJunit3TestCase.class),
				$(TestJUnit4TestCase.class),
				$(TestThatInherits.class),
				$(JUnit3TestThatInherits.class),
				$(JUnit4TestThatInherits.class),
				$(TestWithACustomRunner.class),
				$(ParameterizedTest.class),
				$(TestNGFakeProductTest.class),
				$(TestNGWithClassLevelOnlyTestAnnotationFakeTest.class)
		);
	}
}
