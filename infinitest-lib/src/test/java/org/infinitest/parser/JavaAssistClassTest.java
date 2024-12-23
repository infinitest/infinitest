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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import javax.swing.JTree;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fakeco.fakeproduct.ANewClass;
import com.fakeco.fakeproduct.AnnotatedClass;
import com.fakeco.fakeproduct.ClassAnnotation;
import com.fakeco.fakeproduct.FakeDependency;
import com.fakeco.fakeproduct.FakeProduct;
import com.fakeco.fakeproduct.FakeTree;
import com.fakeco.fakeproduct.FakeUtils;
import com.fakeco.fakeproduct.FieldAnnotation;
import com.fakeco.fakeproduct.InvisibleClassAnnotation;
import com.fakeco.fakeproduct.InvisibleParameterAnnotation;
import com.fakeco.fakeproduct.JUnit5ArchUnitMethodTest;
import com.fakeco.fakeproduct.JUnit5ArchUnitTest;
import com.fakeco.fakeproduct.JUnit5CompositeAnnotationTest;
import com.fakeco.fakeproduct.JUnit5ParameterizedTest;
import com.fakeco.fakeproduct.JUnit5Testable;
import com.fakeco.fakeproduct.JUnit5TestableSubclass;
import com.fakeco.fakeproduct.MethodAnnotation;
import com.fakeco.fakeproduct.ParameterAnnotation;
import com.fakeco.fakeproduct.TestJUnit5TestCase;
import com.fakeco.fakeproduct.id.FakeId;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;

class JavaAssistClassTest {
	private ClassPoolForFakeClassesTestUtil classPoolUtil;

	@BeforeEach
	void inContext() throws NotFoundException {
		classPoolUtil = new ClassPoolForFakeClassesTestUtil();
	}

	@Test
	void shouldFindDependenciesInConstantPool() {
		assertThat(dependenciesOf(FakeProduct.class)).contains(FakeId.class.getName());
		assertThat(dependenciesOf(ANewClass.class)).contains(FakeProduct.class.getName());
		assertThat(dependenciesOf(ANewClass.class)).contains(Integer.class.getName());
	}

	@Test
	void shouldReturnClassNameInToString() {
		assertEquals(FakeProduct.class.getName(), classPoolUtil.getClass(FakeProduct.class).toString());
	}

	@Test
	void shouldDependOnParentClass() {
		assertThat(dependenciesOf(FakeTree.class)).contains(JTree.class.getName());
	}

	@Test
	void shouldFindFieldDependenciesInTheSamePackage() {
		assertThat(dependenciesOf(FakeTree.class)).contains(FakeDependency.class.getName());
	}

	@Test
	void shouldFindMethodDependenciesInTheSameClass() {
		assertThat(dependenciesOf(FakeUtils.class)).contains(Integer.class.getName());
		assertThat(dependenciesOf(FakeUtils.class)).contains(String.class.getName());
	}

	@Test
	void shouldFindDependenciesForClassAnnotations() {
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(ClassAnnotation.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(InvisibleClassAnnotation.class.getName());
	}

	@Test
	void shouldFindDependenciesForFieldAnnotations() {
		assertThat(dependenciesOf(FakeProduct.class)).contains(FieldAnnotation.class.getName());
	}

	@Test
	void shouldFindDependenciesForMethodAnnotations() {
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(org.junit.Test.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(Ignore.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(MethodAnnotation.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(ParameterAnnotation.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(InvisibleParameterAnnotation.class.getName());
	}

	@Test
	void shouldFindDependenciesForJUnit5MethodAnnotations() {
		assertThat(dependenciesOf(TestJUnit5TestCase.class)).contains(org.junit.jupiter.api.Test.class.getName());
	}

	@Test
	void shouldIgnoreTestsWithStrangeOneArgConstructors() throws Exception {
		ClassPool classPool = classPoolUtil.getClassPool();
		CtClass fakeClass = classPool.makeClass("FakeClass");
		CtClass[] params = {classPool.get(Integer.class.getName())};
		fakeClass.addConstructor(new CtConstructor(params, fakeClass));
		assertFalse(new JavaAssistClass(fakeClass).canInstantiate(fakeClass));
	}

	@Test
	void shouldIgnoreTestsWithInvalidParameterNotInClassPool() throws Exception {
		ClassPool classPool = classPoolUtil.getClassPool();
		CtClass fakeClass = classPool.makeClass("FakeClass");
		CtClass notInClassPool = mock(CtClass.class);
		given(notInClassPool.getName()).willReturn("NonClassPoolClass");
		CtClass[] params = {notInClassPool};
		fakeClass.addConstructor(new CtConstructor(params, fakeClass));
		assertFalse(new JavaAssistClass(fakeClass).canInstantiate(fakeClass));
	}

	private String[] dependenciesOf(Class<?> dependingClass) {
		return classPoolUtil.dependenciesOf(dependingClass);
	}
	
	@ParameterizedTest
	@ValueSource(classes = {
			JUnit5ParameterizedTest.class,
			JUnit5CompositeAnnotationTest.class,
			JUnit5Testable.class,
			JUnit5TestableSubclass.class,
			JUnit5ArchUnitTest.class,
			JUnit5ArchUnitMethodTest.class,
			NonPublicArchUnitTest.class,
	})
	void shouldSupportTestClasses(Class<?> testClass) throws NotFoundException {
		ClassPool classPool = classPoolUtil.getClassPool();
		CtClass ctClass = classPool.get(testClass.getName());
		JavaAssistClass javaClass = new JavaAssistClass(ctClass);
		
		assertTrue(javaClass.isATest());
	}
}
