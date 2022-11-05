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

import static org.jgrapht.Graphs.predecessorListOf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.infinitest.environment.ClasspathProvider;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;

public class ClassFileIndex {
	private final JavaClassBuilder builder;
	private DirectedGraph<JavaClass, DefaultEdge> graph;
	private Map<String, JavaClass> classesByName;

	public ClassFileIndex(ClasspathProvider classpath) {
		this(new JavaClassBuilder(classpath));
	}

	@VisibleForTesting
	ClassFileIndex(JavaClassBuilder classBuilder) {
		builder = classBuilder;
		graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		classesByName = new HashMap<>();
	}
	
	public Set<JavaClass> removeClasses(Collection<File> removedFiles) {
		Set<JavaClass> removedClasses = new HashSet<>();
		
		for (File removedFile : removedFiles) {
			JavaClass removedClass = builder.getClass(removedFile);
			if (removedClass != null) {
				graph.removeVertex(removedClass);
				removedClasses.add(removedClass);
			}
		}
		
		return removedClasses;
	}

	public Set<JavaClass> findClasses(Collection<File> changedFiles) {
		// First update class index
		List<String> changedClassesNames = new ArrayList<>();
		for (File changedFile : changedFiles) {
			String changedClassname = builder.classFileChanged(changedFile);
			if (changedClassname != null) {
				changedClassesNames.add(changedClassname);
			}
		}

		// Then find dependencies
		Set<JavaClass> changedClasses = new HashSet<>();
		for (String changedClassesName : changedClassesNames) {
			JavaClass javaClass = builder.getClass(changedClassesName);
			if (javaClass != null) {
				addToIndex(javaClass);
				changedClasses.add(javaClass);
			}
		}
		builder.clear();
		return changedClasses;
	}

	public JavaClass findJavaClass(String classname) {
		JavaClass clazz = findClass(classname);
		if (clazz == null) {
			clazz = builder.getClass(classname);
			if (clazz.locatedInClassFile()) {
				addToIndex(clazz);
			}
		}
		return clazz;
	}

	private JavaClass findClass(String classname) {
		return classesByName.get(classname);
	}

	private void addToIndex(JavaClass newClass) {
		addToGraph(newClass);
		updateParentReferences(newClass);
	}

	private void addToGraph(JavaClass newClass) {
		if (!graph.addVertex(newClass)) {
			replaceVertex(newClass);
		}
		
		classesByName.put(newClass.getName(), newClass);
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
		graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		classesByName.clear();
	}

	public boolean isIndexed(Class<Object> clazz) {
		return classesByName.containsKey(clazz.getName());
	}

	public Set<String> getIndexedClasses() {
		return classesByName.keySet();
	}
}
