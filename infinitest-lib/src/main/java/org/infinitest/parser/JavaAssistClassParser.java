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

import static com.google.common.base.Splitter.*;
import static com.google.common.collect.Lists.*;
import static java.io.File.*;

import java.io.*;
import java.net.*;
import java.util.*;

import javassist.*;

import org.infinitest.*;

import com.google.common.collect.*;
import com.google.common.hash.Hashing;
import com.google.common.io.*;

public class JavaAssistClassParser {
	private final String classpath;
	private ClassPool classPool;

	public JavaAssistClassParser(String classpath) {
		this.classpath = classpath;
	}

	public void clear() {
		// classPool = null;
	}

	private ClassPool getClassPool() {
		if (classPool == null) {
			// This is used primarily for getting Java core objects like String
			// and Integer,
			// so if we don't have the project's JDK classpath, it's probably
			// OK.
			classPool = new ClassPool(true);
			try {
				for (String pathElement : getPathElements()) {
					classPool.appendClassPath(pathElement);
				}
			} catch (NotFoundException e) {
				classPool = null; // RISK Untested
				// Blank out the class pool so we try again next time
				throw new MissingClassException("Could not create class pool", e);
			}
		}
		return classPool;
	}

	private Iterable<String> getPathElements() {
		List<String> entries = newArrayList(on(pathSeparator).split(classpath));
		ListIterator<String> iter = entries.listIterator();
		while (iter.hasNext()) {
			if (entryDoesNotExist(iter)) {
				iter.remove();
			}
		}
		return entries;
	}

	private boolean entryDoesNotExist(ListIterator<String> iter) {
		return !new File(iter.next()).exists();
	}

	private final static Map<String, JavaClass> CLASSES_BY_NAME = Maps.newHashMap();

	public JavaClass getClass(String className) {
		JavaClass clazz = CLASSES_BY_NAME.get(className);
		if (clazz == null) {
			CtClass ctClass = getCachedClass(className);

			if (unparsableClass(ctClass)) {
				clazz = new UnparsableClass(className);
			} else {
				try {
					JavaAssistClass javaAssistClass = new JavaAssistClass(ctClass);
					URL url = getClassPool().find(className);
					if ((url != null) && url.getProtocol().equals("file")) {
						javaAssistClass.setClassFile(new File(url.toURI()));
					}
					clazz = javaAssistClass;
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}

			CLASSES_BY_NAME.put(className, clazz);
		}

		return clazz;
	}

	private final static Map<String, CacheEntry> BY_PATH = Maps.newHashMap();

	public static class CacheEntry {
		final String sha1;
		final String classname;

		public CacheEntry(String sha1, String classname) {
			this.sha1 = sha1;
			this.classname = classname;
		}
	}
	
	/**
	 * @return The {@link JavaClass} corresponding to this file or null if it was not parsed or does not exist
	 */
	public JavaClass getClass(File file) {
		CacheEntry entry = BY_PATH.get(file.getAbsolutePath());
		
		if (entry != null) {
			String classname = entry.classname;
			
			return CLASSES_BY_NAME.get(classname);
		}
		
		return null;
	}

	public String classFileChanged(File file) throws IOException {
		String sha1 = Files.hash(file, Hashing.sha1()).toString();
		CacheEntry entry = BY_PATH.get(file.getAbsolutePath());
		if ((entry != null) && (entry.sha1.equals(sha1))) {
			return entry.classname;
		}

		try (FileInputStream inputStream = new FileInputStream(file)) {
			CtClass ctClass = getClassPool().makeClass(inputStream);
			String classname = ctClass.getName();

			CLASSES_BY_NAME.remove(classname);
			BY_PATH.put(file.getAbsolutePath(), new CacheEntry(sha1, classname));

			return classname;
		}
	}

	private boolean unparsableClass(CtClass cachedClass) {
		return cachedClass.getClassFile2() == null;
	}

	private CtClass getCachedClass(String className) {
		CtClass clazz = getClassPool().getOrNull(className);
		if (clazz == null) {
			throw new MissingClassException("Expected to find " + className);
		}
		return clazz;
	}
}
