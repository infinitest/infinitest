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