package org.infinitest.parser;

import static org.hamcrest.Matchers.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.util.Collection;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;

import javax.swing.JTree;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fakeco.fakeproduct.ANewClass;
import com.fakeco.fakeproduct.AnnotatedClass;
import com.fakeco.fakeproduct.ClassAnnotation;
import com.fakeco.fakeproduct.FakeDependency;
import com.fakeco.fakeproduct.FakeProduct;
import com.fakeco.fakeproduct.FakeTree;
import com.fakeco.fakeproduct.FakeUtils;
import com.fakeco.fakeproduct.FieldAnnotation;
import com.fakeco.fakeproduct.InvisibleClassAnnotation;
import com.fakeco.fakeproduct.InvisibleParameterAnnotation;
import com.fakeco.fakeproduct.MethodAnnotation;
import com.fakeco.fakeproduct.ParameterAnnotation;
import com.fakeco.fakeproduct.id.FakeId;

public class JavaAssistClassTest
{
    private ClassPool classPool;

    @Before
    public void inContext() throws NotFoundException
    {
        classPool = new ClassPool();
        // If you get a NotFoundException here, you may need to make a copy of your jgrapht jar file
        // that lives in a lib subdirectory. Not sure why a scalafied project doesn't parse the
        // .classpath file correctly, but somewhere betweeen there and eclipse that path
        // gets messed up.
        classPool.appendPathList(fakeClasspath().getCompleteClasspath());
        classPool.appendSystemPath();
    }

    @Test
    public void shouldFindDependenciesInConstantPool()
    {
        assertThat(dependenciesOf(FakeProduct.class), hasItem(FakeId.class.getName()));
        assertThat(dependenciesOf(ANewClass.class), hasItem(FakeProduct.class.getName()));
        assertThat(dependenciesOf(ANewClass.class), hasItem(Integer.class.getName()));
    }

    @Test
    public void shouldReturnClassNameInToString()
    {
        assertEquals(FakeProduct.class.getName(), getClass(FakeProduct.class).toString());
    }

    @Test(expected = DisposedClassException.class)
    public void shouldRemoveAllImportsWhenDisposed()
    {
        JavaAssistClass clazz = getClass(FakeProduct.class);
        clazz.dispose();
        clazz.getImports();
    }

    @Test
    public void shouldDependOnParentClass()
    {
        assertThat(dependenciesOf(FakeTree.class), hasItem(JTree.class.getName()));
    }

    @Test
    public void shouldFindFieldDependenciesInTheSamePackage()
    {
        assertThat(dependenciesOf(FakeTree.class), hasItem(FakeDependency.class.getName()));
    }

    @Test
    public void shouldFindMethodDependenciesInTheSameClass()
    {
        assertThat(dependenciesOf(FakeUtils.class), hasItem(Integer.class.getName()));
        assertThat(dependenciesOf(FakeUtils.class), hasItem(String.class.getName()));
    }

    @Test
    public void shouldFindDependenciesForClassAnnotations()
    {
        assertThat(dependenciesOf(AnnotatedClass.class), hasItem(ClassAnnotation.class.getName()));
        assertThat(dependenciesOf(AnnotatedClass.class), hasItem(InvisibleClassAnnotation.class.getName()));
    }

    @Test
    public void shouldFindDependenciesForFieldAnnotations()
    {
        assertThat(dependenciesOf(FakeProduct.class), hasItem(FieldAnnotation.class.getName()));
    }

    @Test
    public void shouldFindDependenciesForMethodAnnotations()
    {
        assertThat(dependenciesOf(AnnotatedClass.class), hasItem(Test.class.getName()));
        assertThat(dependenciesOf(AnnotatedClass.class), hasItem(Ignore.class.getName()));
        assertThat(dependenciesOf(AnnotatedClass.class), hasItem(MethodAnnotation.class.getName()));
        assertThat(dependenciesOf(AnnotatedClass.class), hasItem(ParameterAnnotation.class.getName()));
        assertThat(dependenciesOf(AnnotatedClass.class), hasItem(InvisibleParameterAnnotation.class.getName()));
    }

    @Test
    public void shouldIgnoreTestsWithStrangeOneArgConstructors() throws Exception
    {
        CtClass fakeClass = classPool.makeClass("FakeClass");
        CtClass[] params = { classPool.get(Integer.class.getName()) };
        fakeClass.addConstructor(new CtConstructor(params, fakeClass));
        assertFalse(new JavaAssistClass(fakeClass).canInstantiate(fakeClass));
    }

    private Collection<String> dependenciesOf(Class<?> dependingClass)
    {
        return getClass(dependingClass).getImports();
    }

    private JavaAssistClass getClass(Class<?> dependingClass)
    {
        try
        {
            return new JavaAssistClass(classPool.get(dependingClass.getName()));
        }
        catch (NotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}
