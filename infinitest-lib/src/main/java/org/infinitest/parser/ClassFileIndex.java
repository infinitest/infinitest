/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.parser;

import static com.google.common.collect.Sets.*;
import static org.jgrapht.Graphs.*;

import java.io.*;
import java.util.*;

import org.infinitest.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

import com.google.common.annotations.*;
import com.google.common.collect.*;

public class ClassFileIndex {
	private final ClassBuilder builder;
	private DirectedGraph<JavaClass, DefaultEdge> graph;

	public ClassFileIndex(ClasspathProvider classpath) {
		this(new JavaClassBuilder(classpath));
	}

	@VisibleForTesting
	ClassFileIndex(ClassBuilder classBuilder) {
		builder = classBuilder;
		graph = new DefaultDirectedGraph<JavaClass, DefaultEdge>(DefaultEdge.class);
	}

	public Set<JavaClass> findClasses(Collection<File> changedFiles) {
		Set<JavaClass> changedClasses = newHashSet();
		for (File file : changedFiles) {
			JavaClass javaClass = loadClassFromFile(file);
			if (javaClass != null) {
				changedClasses.add(javaClass);
			}
		}
		builder.clear();
		return changedClasses;
	}

	public JavaClass findJavaClass(String classname) {
		JavaClass clazz = findClass(classname);
		if (clazz == null) {
			clazz = builder.createClass(classname);
			if (clazz.locatedInClassFile()) {
				addToIndex(clazz);
			}
		}
		return clazz;
	}

	private JavaClass findClass(String classname) {
		for (JavaClass jClass : graph.vertexSet()) {
			if (jClass.getName().equals(classname)) {
				return jClass;
			}
		}
		return null;
	}

	private void addToIndex(JavaClass newClass) {
		addToGraph(newClass);
		updateParentReferences(newClass);
		newClass.dispose();
	}

	private void addToGraph(JavaClass newClass) {
		if (!graph.addVertex(newClass)) {
			replaceVertex(newClass);
		}
	}

	private List<JavaClass> getParents(JavaClass childClass) {
		return predecessorListOf(graph, childClass);
	}

	private void replaceVertex(JavaClass newClass) {
		List<JavaClass> incomingEdges = getParents(newClass);

		graph.removeVertex(newClass);
		graph.addVertex(newClass);
		for (JavaClass each : incomingEdges) {
			graph.addEdge(each, newClass);
		}
	}

	private void updateParentReferences(JavaClass parentClass) {
		for (String child : parentClass.getImports()) {
			JavaClass childClass = findJavaClass(child);
			if ((childClass != null) && !childClass.equals(parentClass)) {
				if (graph.containsVertex(childClass)) {
					graph.addEdge(parentClass, childClass);
				}
			}
		}
	}

	JavaClass loadClassFromFile(File file) {
		JavaClass javaClass = builder.loadClass(file);
		if (javaClass != null) {
			addToIndex(javaClass);
		}
		return javaClass;
	}

	// Loop through all changed classes, adding their parents (and their
	// parents)
	// to another set of changed classes
	public Set<JavaClass> findChangedParents(Set<JavaClass> classes) {
		Set<JavaClass> changedParents = Sets.newHashSet(classes);
		for (JavaClass jclass : classes) {
			findParents(jclass, changedParents);
		}
		return changedParents;
	}

	private void findParents(JavaClass jclass, Set<JavaClass> changedParents) {
		for (JavaClass parent : getParents(jclass)) {
			if (changedParents.add(parent)) {
				findParents(parent, changedParents);
			}
		}
	}

	public void clear() {
		graph = new DefaultDirectedGraph<JavaClass, DefaultEdge>(DefaultEdge.class);
	}

	public boolean isIndexed(Class<Object> clazz) {
		return getIndexedClasses().contains(clazz.getName());
	}

	public Set<String> getIndexedClasses() {
		Set<String> classes = newHashSet();
		Set<JavaClass> vertexSet = graph.vertexSet();
		for (JavaClass each : vertexSet) {
			classes.add(each.getName());
		}
		return classes;
	}
}
