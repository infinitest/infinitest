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
import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;

import org.infinitest.*;
import org.infinitest.filter.*;
import org.infinitest.util.*;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.io.*;

/**
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 */
public class ClassFileTestDetector implements TestDetector {
	private final TestFilter filters;
	private ClassFileIndex index;
	private ClasspathProvider classpath;

	public ClassFileTestDetector(TestFilter testFilterList) {
		filters = testFilterList;
	}

	@Override
	public void clear() {
		index.clear();
	}

	/**
	 * Runs through the classpath looking for changed files and returns the set
	 * of tests that need to be run.
	 */
	@Override
	public synchronized Set<JavaClass> findTestsToRun(Collection<File> changedFiles) {

		InfinitestUtils.log(changedFiles.toString());

		filters.updateFilterList();

		final List<String> refs = new ArrayList<String>();
		for (File changedFile : changedFiles) {
			if (changedFile.isFile() && !changedFile.getName().endsWith(".java") && !changedFile.getName().endsWith(".class")) {
				refs.add(changedFile.getName());
			}
		}

		Collection<File> filteredJavaFiles = Collections2.filter(getJavaFiles(), new Predicate<File>() {
			public boolean apply(File input) {
				try {
					return fileContainsRef(input, refs);
				} catch (IOException e) {
					InfinitestUtils.log("Oups", e);
					return false;
				}
			}
		});

		InfinitestUtils.log("Java files find : " + filteredJavaFiles);
		InfinitestUtils.log("Changed file : " + changedFiles);

		// Find changed classes
		Set<JavaClass> changedClasses = index.findClasses(changedFiles);
		// Find classes depend on file changed.
		// FIXME : error on multiple class on different package with same name.
		for (File file : filteredJavaFiles) {
			changedClasses.add(index.findJavaClass(file.getName().split("\\.")[0]));
		}

		// Add to changedClasses.

		Set<JavaClass> changedParents = index.findChangedParents(changedClasses);

		// combine two sets
		changedClasses.addAll(changedParents);

		// run through total set, and pick out tests to run
		log(Level.FINE, "Total changeset: " + changedParents);
		return filterTests(changedClasses);
	}

	private boolean fileContainsRef(File javaFile, Collection<String> refs) throws IOException {
		String contenuFichier = Files.toString(javaFile, Charset.defaultCharset());

		for (String ref : refs) {
			if (contenuFichier.contains(ref)) {
				return true;
			}
		}
		return false;
	}

	private List<File> getAllFiles(File root) {
		List<File> fichiers = new ArrayList<File>(Arrays.asList(root.listFiles()));
		for (File fichier : new ArrayList<File>(fichiers)) {
			if (fichier.isDirectory()) {
				fichiers.addAll(getAllFiles(fichier));
			}
		}
		return fichiers;
	}

	private List<File> getJavaFiles() {
		List<File> allFiles = getAllFiles(classpath.getWorkingDirectory());
		List<File> javaFiles = new ArrayList<File>();
		for (File file : allFiles) {
			if (file.isFile() && file.getName().endsWith(".java")) {
				javaFiles.add(file);
			}
		}
		return javaFiles;
	}

	private Set<JavaClass> filterTests(Set<JavaClass> changedClasses) {
		Set<JavaClass> testsToRun = new HashSet<JavaClass>();
		for (JavaClass jclass : changedClasses) {
			if (isATest(jclass) && inCurrentProject(jclass)) {
				testsToRun.add(jclass);
			} else {
				log(Level.FINE, "Filtered test: " + jclass);
			}
		}
		return testsToRun;
	}

	private boolean inCurrentProject(JavaClass jclass) {
		// I can't find a scenario where a non-classfile could get in here, but
		// I think I'm missing
		// it, so I still want to guard against it
		if (jclass.locatedInClassFile()) {
			File classFile = jclass.getClassFile();
			for (File parentDir : classpath.getClassOutputDirs()) {
				if (classFile.getAbsolutePath().startsWith(parentDir.getAbsolutePath())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isATest(JavaClass jclass) {
		return jclass.isATest() && !filters.match(jclass);
	}

	boolean isIndexed(Class<Object> clazz) {
		return index.isIndexed(clazz);
	}

	public Set<String> getIndexedClasses() {
		return index.getIndexedClasses();
	}

	public JavaClass findJavaClass(String name) {
		return index.findJavaClass(name);
	}

	@Override
	public void setClasspathProvider(ClasspathProvider classpath) {
		this.classpath = classpath;
		index = new ClassFileIndex(classpath);
	}

	@Override
	public Set<String> getCurrentTests() {
		Set<String> tests = newHashSet();
		for (String each : getIndexedClasses()) {
			if (isATest(index.findJavaClass(each))) {
				tests.add(each);
			}
		}
		return tests;
	}
}
