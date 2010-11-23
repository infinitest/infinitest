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

import static com.google.common.collect.Sets.*;
import static org.jgrapht.Graphs.*;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.infinitest.ClasspathProvider;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;

public class ClassFileIndex
{
    private final ClassBuilder builder;
    private DirectedGraph<JavaClass, DefaultEdge> graph;

    public ClassFileIndex(ClasspathProvider classpath)
    {
        this(new JavaClassBuilder(classpath));
    }

    @VisibleForTesting
    ClassFileIndex(ClassBuilder classBuilder)
    {
        builder = classBuilder;
        graph = new DefaultDirectedGraph<JavaClass, DefaultEdge>(DefaultEdge.class);
    }

    public Set<JavaClass> findClasses(Collection<File> changedFiles)
    {
        Set<JavaClass> changedClasses = newHashSet();
        for (File file : changedFiles)
        {
            JavaClass javaClass = loadClassFromFile(file);
            if (javaClass != null)
            {
                changedClasses.add(javaClass);
            }
        }
        builder.clear();
        return changedClasses;
    }

    public JavaClass findJavaClass(String classname)
    {
        JavaClass clazz = findClass(classname);
        if (clazz == null)
        {
            clazz = builder.createClass(classname);
            if (clazz.locatedInClassFile())
            {
                addToIndex(clazz);
            }
        }
        return clazz;
    }

    private JavaClass findClass(String classname)
    {
        for (JavaClass jClass : graph.vertexSet())
        {
            if (jClass.getName().equals(classname))
            {
                return jClass;
            }
        }
        return null;
    }

    private void addToIndex(JavaClass newClass)
    {
        addToGraph(newClass);
        updateParentReferences(newClass);
        newClass.dispose();
    }

    private void addToGraph(JavaClass newClass)
    {
        if (!graph.addVertex(newClass))
        {
            replaceVertex(newClass);
        }
    }

    private List<JavaClass> getParents(JavaClass childClass)
    {
        return predecessorListOf(graph, childClass);
    }

    private void replaceVertex(JavaClass newClass)
    {
        List<JavaClass> incomingEdges = getParents(newClass);

        graph.removeVertex(newClass);
        graph.addVertex(newClass);
        for (JavaClass each : incomingEdges)
        {
            graph.addEdge(each, newClass);
        }
    }

    private void updateParentReferences(JavaClass parentClass)
    {
        for (String child : parentClass.getImports())
        {
            JavaClass childClass = findJavaClass(child);
            if (childClass != null && !childClass.equals(parentClass))
            {
                if (graph.containsVertex(childClass))
                {
                    graph.addEdge(parentClass, childClass);
                }
            }
        }
    }

    JavaClass loadClassFromFile(File file)
    {
        JavaClass javaClass = builder.loadClass(file);
        if (javaClass != null)
        {
            addToIndex(javaClass);
        }
        return javaClass;
    }

    // Loop through all changed classes, adding their parents (and their parents)
    // to another set of changed classes
    public Set<JavaClass> findChangedParents(Set<JavaClass> classes)
    {
        Set<JavaClass> changedParents = Sets.newHashSet(classes);
        for (JavaClass jclass : classes)
        {
            findParents(jclass, changedParents);
        }
        return changedParents;
    }

    private void findParents(JavaClass jclass, Set<JavaClass> changedParents)
    {
        for (JavaClass parent : getParents(jclass))
        {
            if (changedParents.add(parent))
            {
                findParents(parent, changedParents);
            }
        }
    }

    public void clear()
    {
        graph = new DefaultDirectedGraph<JavaClass, DefaultEdge>(DefaultEdge.class);
    }

    public boolean isIndexed(Class<Object> clazz)
    {
        return getIndexedClasses().contains(clazz.getName());
    }

    public boolean isEmpty()
    {
        return graph.vertexSet().isEmpty();
    }

    public Set<String> getIndexedClasses()
    {
        Set<String> classes = newHashSet();
        Set<JavaClass> vertexSet = graph.vertexSet();
        for (JavaClass each : vertexSet)
        {
            classes.add(each.getName());
        }
        return classes;
    }
}
