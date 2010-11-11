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

public class ClassFileIndex
{
    private ClassBuilder builder;
    private DirectedGraph<JavaClass, DefaultEdge> graph;

    public ClassFileIndex(ClasspathProvider classpath)
    {
        builder = new JavaClassBuilder(classpath);
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
        for (JavaClass each : graph.vertexSet())
        {
            if (each.getName().equals(classname))
            {
                return each;
            }
        }
        return null;
    }

    private Collection<JavaClass> getParents(JavaClass childClass)
    {
        return predecessorListOf(graph, childClass);
    }

    private void addToIndex(JavaClass newClass)
    {
        addToGraph(newClass);
        updateParentReferences(newClass);
        newClass.dispose();
    }

    private void addToGraph(JavaClass newClass)
    {
        if (graph.containsVertex(newClass))
        {
            replaceVertex(newClass);
        }
        else
        {
            graph.addVertex(newClass);
        }
    }

    private void replaceVertex(JavaClass newClass)
    {
        List<JavaClass> incomingEdges = predecessorListOf(graph, newClass);
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

    void findParents(Set<JavaClass> classes, Set<JavaClass> parents, JavaClass jclass)
    {
        parents.add(jclass);
        for (JavaClass parentClass : getParents(jclass))
        {
            // If a parent class hasn't been checked before
            // (and it's not already marked to be run),
            // add it to the list of changed classes
            if (!classes.contains(parentClass) && !parents.contains(parentClass) && !parentClass.equals(jclass))
            {
                findParents(classes, parents, parentClass);
            }
        }
    }

    public synchronized void clear()
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

    void setBuilder(ClassBuilder builder)
    {
        this.builder = builder;
    }
}
