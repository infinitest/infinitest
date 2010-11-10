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
