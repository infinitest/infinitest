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

import static org.fest.assertions.Assertions.assertThat;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import javassist.*;
import javax.swing.*;

import org.junit.*;

import com.fakeco.fakeproduct.*;
import com.fakeco.fakeproduct.id.*;

public class JavaAssistClassTest {
	private ClassPool classPool;

	@Before
	public void inContext() throws NotFoundException {
		classPool = new ClassPool();
		// If you get a NotFoundException here, you may need to make a copy of
		// your jgrapht jar file
		// that lives in a lib subdirectory. Not sure why a scalafied project
		// doesn't parse the
		// .classpath file correctly, but somewhere betweeen there and eclipse
		// that path
		// gets messed up.
		classPool.appendPathList(fakeClasspath().getCompleteClasspath());
		classPool.appendSystemPath();
	}

	@Test
	public void shouldFindDependenciesInConstantPool() {
		assertThat(dependenciesOf(FakeProduct.class)).contains(FakeId.class.getName());
		assertThat(dependenciesOf(ANewClass.class)).contains(FakeProduct.class.getName());
		assertThat(dependenciesOf(ANewClass.class)).contains(Integer.class.getName());
	}

	@Test
	public void shouldReturnClassNameInToString() {
		assertEquals(FakeProduct.class.getName(), getClass(FakeProduct.class).toString());
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
	public void shouldIgnoreTestsWithStrangeOneArgConstructors() throws Exception {
		CtClass fakeClass = classPool.makeClass("FakeClass");
		CtClass[] params = {classPool.get(Integer.class.getName())};
		fakeClass.addConstructor(new CtConstructor(params, fakeClass));
		assertFalse(new JavaAssistClass(fakeClass).canInstantiate(fakeClass));
	}

	private String[] dependenciesOf(Class<?> dependingClass) {
		return getClass(dependingClass).getImports();
	}

	private JavaAssistClass getClass(Class<?> dependingClass) {
		try {
			return new JavaAssistClass(classPool.get(dependingClass.getName()));
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
