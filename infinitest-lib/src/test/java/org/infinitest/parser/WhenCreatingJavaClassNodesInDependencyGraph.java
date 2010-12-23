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

import static java.io.File.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewConstructor;
import javassist.NotFoundException;

import org.infinitest.ClasspathProvider;
import org.infinitest.StandaloneClasspath;
import org.infinitest.util.FakeEnvironments;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fakeco.fakeproduct.FakeDependency;
import com.fakeco.fakeproduct.FakeTree;

public class WhenCreatingJavaClassNodesInDependencyGraph
{
    private JavaClassBuilder builder;
    private File newDir;

    @Before
    public void inContext()
    {
        ClasspathProvider classpath = fakeClasspath();
        builder = new JavaClassBuilder(classpath);
    }

    @After
    public void cleanup()
    {
        if (newDir != null)
        {
            delete(newDir);
        }
    }

    private static void delete(File directory)
    {
        for (File file : directory.listFiles())
        {
            if (file.isDirectory())
            {
                delete(file);
            }
            else
            {
                assertTrue(file.delete());
            }
        }
        assertTrue(directory.delete());
    }

    @Test
    public void shouldReturnUnparsableClassIfClassCannotBeFound()
    {
        JavaClass javaClass = builder.createClass("foo.bar.com");
        assertThat(javaClass, instanceOf(UnparsableClass.class));
        assertEquals("foo.bar.com", javaClass.getName());
        assertEquals(emptyList(), javaClass.getImports());
    }

    @Test
    public void shouldReturnUnparsableClassIfErrorOccursWhileParsing()
    {
        ClassParser parser = mock(ClassParser.class);
        when(parser.getClass("MyClassName")).thenThrow(new RuntimeException(new NotFoundException("")));

        builder = new JavaClassBuilder(parser);

        assertThat(builder.createClass("MyClassName"), instanceOf(UnparsableClass.class));
    }

    @Test
    public void shouldLookForClassesInTargetDirectories() throws Exception
    {
        newDir = new File("tempClassDir");
        List<File> buildPaths = asList(newDir);
        ClasspathProvider classpath = new StandaloneClasspath(buildPaths, FakeEnvironments.systemClasspath()
                        + pathSeparator + newDir.getAbsolutePath());

        String classname = "org.fakeco.Foobar";
        createClass(classname);

        builder = new JavaClassBuilder(classpath);
        JavaClass javaClass = builder.createClass(classname);
        assertEquals(classname, javaClass.getName());
        assertFalse(javaClass.isATest());
    }

    @Test
    public void shouldAlsoLookForClassesInClassDirectories() throws Exception
    {
        newDir = new File("tempClassDir");
        List<File> buildPaths = asList(newDir);
        ClasspathProvider classpath = new StandaloneClasspath(Collections.<File> emptyList(), buildPaths,
                        FakeEnvironments.systemClasspath() + pathSeparator + newDir.getAbsolutePath());

        String classname = "org.fakeco.Foobar2";
        createClass(classname);

        builder = new JavaClassBuilder(classpath);
        JavaClass javaClass = builder.createClass(classname);
        assertEquals(classname, javaClass.getName());
        assertFalse(javaClass.isATest());
    }

    private void createClass(String classname) throws CannotCompileException, IOException
    {
        ClassPool pool = ClassPool.getDefault();
        CtClass foobarClass = pool.makeClass(classname);
        foobarClass.writeFile(newDir.getAbsolutePath());
    }

    @Test
    public void shouldFindDependenciesInSamePackage()
    {
        JavaClass javaClass = builder.createClass(FakeTree.class.getName());
        assertThat(javaClass.getImports(), hasItem(FakeDependency.class.getName()));
    }

    public static void main(String[] args) throws Exception
    {
        ClassPool pool = ClassPool.getDefault();
        String classname = "org.fakeco.FoobarParent";
        CtClass parentClass = pool.makeClass(classname);
        parentClass.addConstructor(CtNewConstructor.defaultConstructor(parentClass));
        parentClass.writeFile();
        Runtime.getRuntime().exec("jar c org > binLib/foobarParent.jar").waitFor();
    }
}
