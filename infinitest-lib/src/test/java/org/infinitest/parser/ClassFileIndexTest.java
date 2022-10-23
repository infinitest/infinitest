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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.environment.FakeEnvironments.fakeClasspath;
import static org.infinitest.util.InfinitestTestUtils.getFileForClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.FakeProduct;

class ClassFileIndexTest {
	private ClassFileIndex index;
	private JavaClassBuilder builder;

	@BeforeEach
	void inContext() {
		builder = mock(JavaClassBuilder.class);
		index = new ClassFileIndex(builder);
	}

	@Test
	void shouldClearClassBuilderAfterLookingForJavaFilesToReduceMemoryFootprint() {
		when(builder.getClass("")).thenReturn(new FakeJavaClass(""));

		index.findClasses(asList(getFileForClass(FakeProduct.class)));

		verify(builder).clear();
	}

	@Test
	void shouldIgnoreClassFilesThatCannotBeParsed() {
		ClassFileIndex index = new ClassFileIndex(fakeClasspath());
		assertEquals(Collections.emptySet(), index.findClasses(Collections.singleton(new File("notAClassFile"))));
	}
	
	@Test
	public void removeFiles() {
		JavaClass javaClass = mock(JavaClass.class);
		when(builder.getClass(any(File.class))).thenReturn(javaClass);
		
		Set<JavaClass> removedClasses = index.removeClasses(Collections.singleton(new File("")));
		
		assertThat(removedClasses).contains(javaClass);
	}
}
