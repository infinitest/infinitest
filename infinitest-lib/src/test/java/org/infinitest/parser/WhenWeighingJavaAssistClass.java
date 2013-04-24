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

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.*;

import com.google.common.cache.*;

public class WhenWeighingJavaAssistClass {
	private final Weigher<String, JavaAssistClass> weighter = new JavaAssistClassWeigher();

	@Test
	public void class_with_no_import_should_weight_its_key() {
		JavaAssistClass clazz = classWithImports();

		int weight = weighter.weigh("KEY", clazz);

		assertThat(weight).isEqualTo("KEY".length()).isEqualTo(3);
	}

	@Test
	public void each_import_should_weight_its_length() {
		JavaAssistClass clazz = classWithImports("import", "otherimport");

		int weight = weighter.weigh("", clazz);

		assertThat(weight).isEqualTo("import".length() + "otherimport".length()).isEqualTo(17);
	}

	@Test
	public void sum_up_key_length_and_imports_length() {
		JavaAssistClass clazz = classWithImports("import");

		int weight = weighter.weigh("OTHER_KEY", clazz);

		assertThat(weight).isEqualTo("OTHER_KEY".length() + "import".length()).isEqualTo(15);
	}

	static JavaAssistClass classWithImports(String... imports) {
		JavaAssistClass clazz = mock(JavaAssistClass.class);
		when(clazz.getImports()).thenReturn(imports);
		return clazz;
	}
}
