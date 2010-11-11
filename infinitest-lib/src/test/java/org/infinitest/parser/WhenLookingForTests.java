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
package org.infinitest.parser;

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;
import javassist.ClassPool;
import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;

import com.fakeco.fakeproduct.AbstractTestFakeProduct;
import com.fakeco.fakeproduct.AllTests;
import com.fakeco.fakeproduct.ClassWithAnInnerTestClass;
import com.fakeco.fakeproduct.JUnit3TestThatInherits;
import com.fakeco.fakeproduct.JUnit4TestThatInherits;
import com.fakeco.fakeproduct.LooksLikeAJUnit3TestButIsnt;
import com.fakeco.fakeproduct.ParameterizedTest;
import com.fakeco.fakeproduct.TestJunit3TestCase;
import com.fakeco.fakeproduct.TestThatInheritsACustomRunner;
import com.fakeco.fakeproduct.TestWithACustomRunner;
import com.fakeco.fakeproduct.ValidTestWithUnconventionalConstructor;

public class WhenLookingForTests
{
    private ClassPool classPool;

    @Before
    public void inContext() throws NotFoundException
    {
        classPool = new ClassPool();
        classPool.appendPathList(fakeClasspath().getCompleteClasspath());
        classPool.appendSystemPath();
    }

    @Test
    public void shouldIgnoreAbstractClasses()
    {
        assertFalse(classFor(AbstractTestFakeProduct.class).isATest());
    }

    @Test
    public void shouldIgnoreInnerClasses()
    {
        assertFalse(classFor(ClassWithAnInnerTestClass.InnerTest.class).isATest());
    }

    @Test
    public void shouldIgnoreTestCasesWithSuiteMethods()
    {
        assertFalse(classFor(AllTests.class).isATest());
    }

    @Test
    public void shouldDetectParameterizedTestsWithAlternateConstructors()
    {
        assertTrue(classFor(ParameterizedTest.class).isATest());
    }

    @Test
    public void shouldDetectClassesThatInheritFromTests()
    {
        assertTrue(classFor(JUnit4TestThatInherits.class).isATest());
        assertTrue(classFor(JUnit3TestThatInherits.class).isATest());
    }

    @Test
    public void canDetectTestsWithCustomRunners()
    {
        assertTrue(classFor(TestWithACustomRunner.class).isATest());
    }

    @Test
    public void shouldDetectClassesThatInheritCustomRunners()
    {
        assertTrue(classFor(TestThatInheritsACustomRunner.class).isATest());
    }

    @Test
    public void shouldDetectTestThatInheritARunnerAndHasUnconventionalConstructor()
    {
        assertTrue(classFor(ValidTestWithUnconventionalConstructor.class).isATest());
    }

    @Test
    public void canDetectJUnit3Tests()
    {
        assertTrue(classFor(TestJunit3TestCase.class).isATest());
        assertFalse(classFor(LooksLikeAJUnit3TestButIsnt.class).isATest());
    }

    private JavaAssistClass classFor(Class<?> testClass)
    {
        try
        {
            return new JavaAssistClass(classPool.get(testClass.getName()));
        }
        catch (NotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}
