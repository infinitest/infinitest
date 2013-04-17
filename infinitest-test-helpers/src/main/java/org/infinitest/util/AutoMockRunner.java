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
package org.infinitest.util;

import static java.lang.reflect.Modifier.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.*;

import org.junit.runners.*;
import org.junit.runners.model.*;
import org.mockito.*;
import org.mockito.exceptions.base.*;

public class AutoMockRunner extends BlockJUnit4ClassRunner {
	private static final String MOCK_PREFIX = "mock";

	public AutoMockRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		try {
			for (Field field : test.getClass().getDeclaredFields()) {
				if (field.getName().startsWith(MOCK_PREFIX) //
						&& !isStatic(field.getModifiers()) //
						&& !isFinal(field.getModifiers())) {
					setFieldValue(test, field, mock(field));
				}
			}
		} catch (MockitoException e) {
			throw new RuntimeException("Unable to initialize mocks in class: " + test.getClass().getSimpleName(), e);
		}

		return test;
	}

	private static Object mock(Field field) {
		return Mockito.mock(field.getType(), withSettings().name(field.getName()).defaultAnswer(RETURNS_DEFAULTS));
	}

	private void setFieldValue(Object test, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(test, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to access a field", e);
		}
	}
}