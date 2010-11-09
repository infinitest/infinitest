package org.infinitest.parser;

import static org.hamcrest.Matchers.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;

import org.infinitest.util.FakeEnvironments;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fakeco.fakeproduct.TestAlmostNotATest;

public class WhenClassesChange extends DependencyGraphTestBase
{
    private File backup;

    @Before
    public void inContext() throws Exception
    {
        backup = createBackup(TestAlmostNotATest.class.getName());
    }

    @After
    public void cleanupContext()
    {
        restoreFromBackup(backup);
    }

    @Test
    public void shouldRecognizeWhenTestsAreChangedToRegularClasses() throws Exception
    {
        Class<?> testClass = TestAlmostNotATest.class;
        JavaClass javaClass = runTest(testClass);
        assertTrue("Inital state is incorrect", javaClass.isATest());

        untestify();
        updateGraphWithChangedClass(testClass);
        assertThat(getGraph().getCurrentTests(), equalTo(Collections.<String> emptySet()));

        javaClass = getGraph().findJavaClass(testClass.getName());
        assertFalse("Class was not reloaded", javaClass.isATest());
    }

    private JavaClass runTest(Class<?> testClass)
    {
        Set<JavaClass> classes = updateGraphWithChangedClass(testClass);
        assertEquals(1, classes.size());	
        return classes.iterator().next();
    }

    private Set<JavaClass> updateGraphWithChangedClass(Class<?> testClass)
    {
        File classFile = getFileForClass(testClass);
        return getGraph().findTestsToRun(setify(classFile));
    }

    private void untestify() throws Exception
    {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass(TestAlmostNotATest.class.getName());
        cc.setSuperclass(pool.get(Object.class.getName()));
        cc.writeFile(FakeEnvironments.fakeClassDirectory().getAbsolutePath());
    }
}
