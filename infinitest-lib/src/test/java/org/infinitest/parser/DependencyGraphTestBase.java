package org.infinitest.parser;

import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import org.infinitest.filter.FilterStub;
import org.infinitest.util.FakeEnvironments;
import org.infinitest.util.InfinitestTestUtils;
import org.junit.After;
import org.junit.Before;

public abstract class DependencyGraphTestBase
{
    private ClassFileTestDetector testDetector = null;
    private FilterStub filter;

    @Before
    public final void setUp()
    {
        filter = new FilterStub();
        testDetector = new ClassFileTestDetector(filter);
        testDetector.setClasspathProvider(fakeClasspath());
        testDetector.findTestsToRun(Collections.<File> emptySet());
    }

    @After
    public final void tearDown()
    {
        testDetector = null;
    }

    protected Set<JavaClass> findTestsForChangedFiles(Class<?>... classes)
    {
        Set<File> fileSet = new HashSet<File>();
        for (Class<?> clazz : classes)
        {
            fileSet.add(getFileForClass(clazz));
        }
        return getGraph().findTestsToRun(fileSet);
    }

    protected void addToDependencyGraph(Class<?>... classes)
    {
        findTestsForChangedFiles(classes);
    }

    protected void addDependency(String parent, String child) throws NotFoundException, CannotCompileException,
                    IOException
    {
        ClassPool pool = ClassPool.getDefault();
        CtClass parentClass = pool.get(parent);
        CtClass childClass = pool.get(child);
        parentClass.addField(new CtField(childClass, "dep", parentClass));
        parentClass.writeFile(FakeEnvironments.fakeClassDirectory().getAbsolutePath());
    }

    protected void assertClassRecognizedAsTest(Class<?> testClass)
    {
        Set<File> fileSet = setify(InfinitestTestUtils.getFileForClass(testClass));
        Set<JavaClass> testsToRun = getGraph().findTestsToRun(fileSet);
        assertEquals(testClass.getSimpleName() + " should have been recognized as a test", 1, testsToRun.size());
        JavaClass testToRun = testsToRun.iterator().next();
        assertEquals(testClass.getName(), testToRun.getName());
        assertTrue(testToRun.isATest());
    }

    protected void verifyDependency(Class<?> changedFile, Class<?> expectedTest)
    {
        Set<JavaClass> testsToRun = findTestsForChangedFiles(changedFile);
        assertTrue("Changing " + changedFile + " did not cause " + expectedTest + " to be run",
                        testsToRun.contains(getGraph().findJavaClass(expectedTest.getName())));
    }

    protected ClassFileTestDetector getGraph()
    {
        return testDetector;
    }

    protected void addFilter(String className)
    {
        filter.addClass(className);
    }

}
