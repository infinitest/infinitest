/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Stolen from JUnit
 */
class TestClass
{
    private final Class<?> fClass;

    public TestClass(Class<?> klass)
    {
        fClass = klass;
    }

    public List<Method> getAnnotatedMethods(Class<? extends Annotation> annotationClass)
    {
        List<Method> results = new ArrayList<Method>();
        for (Class<?> eachClass : getSuperClasses(fClass))
        {
            Method[] methods = eachClass.getDeclaredMethods();
            for (Method eachMethod : methods)
            {
                Annotation annotation = eachMethod.getAnnotation(annotationClass);
                if (annotation != null && !isShadowed(eachMethod, results))
                {
                    results.add(eachMethod);
                }
            }
        }
        if (runsTopToBottom(annotationClass))
        {
            Collections.reverse(results);
        }
        return results;
    }

    private boolean runsTopToBottom(Class<? extends Annotation> annotation)
    {
        return annotation.equals(Before.class) || annotation.equals(BeforeClass.class);
    }

    private boolean isShadowed(Method method, List<Method> results)
    {
        for (Method each : results)
        {
            if (isShadowed(method, each))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isShadowed(Method current, Method previous)
    {
        if (!previous.getName().equals(current.getName()))
        {
            return false;
        }
        if (previous.getParameterTypes().length != current.getParameterTypes().length)
        {
            return false;
        }
        for (int i = 0; i < previous.getParameterTypes().length; i++)
        {
            if (!previous.getParameterTypes()[i].equals(current.getParameterTypes()[i]))
            {
                return false;
            }
        }
        return true;
    }

    private List<Class<?>> getSuperClasses(Class<?> testClass)
    {
        ArrayList<Class<?>> results = new ArrayList<Class<?>>();
        Class<?> current = testClass;
        while (current != null)
        {
            results.add(current);
            current = current.getSuperclass();
        }
        return results;
    }
}
