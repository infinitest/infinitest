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
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import javassist.*;
import javax.swing.*;

import org.junit.*;

import com.fakeco.fakeproduct.*;
import com.fakeco.fakeproduct.id.*;

public class JavaAssistClassTest {
	private ClassPoolForFakeClassesTestUtil classPoolUtil;

	@Before
	public void inContext() throws NotFoundException {
		classPoolUtil = new ClassPoolForFakeClassesTestUtil();
	}

	@Test
	public void shouldFindDependenciesInConstantPool() {
		assertThat(dependenciesOf(FakeProduct.class)).contains(FakeId.class.getName());
		assertThat(dependenciesOf(ANewClass.class)).contains(FakeProduct.class.getName());
		assertThat(dependenciesOf(ANewClass.class)).contains(Integer.class.getName());
	}

	@Test
	public void shouldReturnClassNameInToString() {
		assertEquals(FakeProduct.class.getName(), classPoolUtil.getClass(FakeProduct.class).toString());
	}

	@Test
	public void shouldDependOnParentClass() {
		assertThat(dependenciesOf(FakeTree.class)).contains(JTree.class.getName());
	}

	@Test
	public void shouldFindFieldDependenciesInTheSamePackage() {
		assertThat(dependenciesOf(FakeTree.class)).contains(FakeDependency.class.getName());
	}

	@Test
	public void shouldFindMethodDependenciesInTheSameClass() {
		assertThat(dependenciesOf(FakeUtils.class)).contains(Integer.class.getName());
		assertThat(dependenciesOf(FakeUtils.class)).contains(String.class.getName());
	}

	@Test
	public void shouldFindDependenciesForClassAnnotations() {
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(ClassAnnotation.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(InvisibleClassAnnotation.class.getName());
	}

	@Test
	public void shouldFindDependenciesForFieldAnnotations() {
		assertThat(dependenciesOf(FakeProduct.class)).contains(FieldAnnotation.class.getName());
	}

	@Test
	public void shouldFindDependenciesForMethodAnnotations() {
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(Test.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(Ignore.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(MethodAnnotation.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(ParameterAnnotation.class.getName());
		assertThat(dependenciesOf(AnnotatedClass.class)).contains(InvisibleParameterAnnotation.class.getName());
	}

	@Test
	public void shouldFindDependenciesForJUnit5MethodAnnotations() {
		assertThat(dependenciesOf(TestJUnit5TestCase.class)).contains(org.junit.jupiter.api.Test.class.getName());
	}

	@Test
	public void shouldIgnoreTestsWithStrangeOneArgConstructors() throws Exception {
		ClassPool classPool = classPoolUtil.getClassPool();
		CtClass fakeClass = classPool.makeClass("FakeClass");
		CtClass[] params = {classPool.get(Integer.class.getName())};
		fakeClass.addConstructor(new CtConstructor(params, fakeClass));
		assertFalse(new JavaAssistClass(fakeClass).canInstantiate(fakeClass));
	}

	@Test
	public void shouldIgnoreTestsWithInvalidParameterNotInClassPool() throws Exception {
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
}
