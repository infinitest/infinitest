package org.infinitest.parser;

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;

import org.infinitest.MissingClassException;
import org.junit.Before;
import org.junit.Test;

import com.fakeco.fakeproduct.AnnotatedClass;
import com.fakeco.fakeproduct.ClassAnnotation;
import com.fakeco.fakeproduct.FakeProduct;
import com.fakeco.fakeproduct.FieldAnnotation;
import com.fakeco.fakeproduct.MethodAnnotation;
import com.fakeco.fakeproduct.ParameterAnnotation;

public class WhenParsingClassFiles
{
    private ClassParser parser;

    @Before
    public void inContext()
    {
        parser = new JavaAssistClassParser(fakeClasspath().getCompleteClasspath());
    }

    private JavaClass parseClass(Class<?> classToParse)
    {
        return parser.getClass(classToParse.getName());
    }

    @Test
    public void shouldLazilyCreateClassPool()
    {
        new JavaAssistClassParser("doesNotExist.jar");
    }

    @Test(expected = MissingClassException.class)
    public void shouldThrowMissingClassExceptionIfClasspathElementsCannotBeFound()
    {
        JavaAssistClassParser classParser = new JavaAssistClassParser("doesNotExist.jar");
        classParser.getClass("doesn't matter");
    }

    @Test
    public void shouldIncludeSystemClasspathInClasspool()
    {
        JavaClass stringClass = parseClass(String.class);
        assertEquals(String.class.getName(), stringClass.getName());
    }

    @Test
    public void shouldAddImportsFromAnnotations()
    {
        JavaClass clazz = parseClass(AnnotatedClass.class);
        assertEquals(AnnotatedClass.class.getName(), clazz.getName());
        Collection<String> imports = clazz.getImports();
        assertTrue(imports.contains(MethodAnnotation.class.getName()));
        assertTrue(imports.contains(ParameterAnnotation.class.getName()));
        assertTrue(imports.contains(ClassAnnotation.class.getName()));
    }

    @Test
    public void shouldDetectFieldAnnotations()
    {
        JavaClass javaClass = parseClass(FakeProduct.class);
        Collection<String> imports = javaClass.getImports();
        assertTrue(imports.contains(FieldAnnotation.class.getName()));
    }

    @Test
    public void shouldSkipMissingJarFilesWhenCreatingClassPool()
    {
        String classpath = fakeClasspath().getCompleteClasspath();
        classpath += File.pathSeparator + "notAJar.jar";
        parser = new JavaAssistClassParser(classpath);
        assertNotNull(parseClass(FakeProduct.class));
    }

    @Test
    public void shouldHandleMissingClassDirs()
    {
        parser = new JavaAssistClassParser("notADirYet");
    }
}
