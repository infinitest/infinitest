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

import com.beust.jcommander.internal.*;
import com.google.common.hash.*;

public class JavaAssistClassParser implements ClassParser {
	private final static Map<String, JavaAssistClass> classesByHash = Maps.newHashMap();

	private final String classpath;
	private ClassPool classPool;

	public JavaAssistClassParser(String classpath) {
		this.classpath = classpath;
	}

	private ClassPool getClassPool() {
		if (classPool == null) {
			classPool = new ClassPool();
			// This is used primarily for getting Java core objects like String
			// and Integer,
			// so if we don't have the project's JDK classpath, it's probably
			// OK.
			classPool.appendSystemPath();
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

	@Override
	public JavaClass getClass(String className) {
		CtClass ctClass = getCachedClass(className);
		if (unparsableClass(ctClass)) {
			return new UnparsableClass(className);
		}

		String hash = hash(ctClass);
		JavaAssistClass clazz = classesByHash.get(hash);
		if (clazz == null) {
			clazz = new JavaAssistClass(ctClass);
			URL url = getClassPool().find(className);
			if ((url != null) && url.getProtocol().equals("file")) {
				clazz.setClassFile(new File(url.getFile()));
			}

			classesByHash.put(hash, clazz);
		}

		return clazz;
	}

	private boolean unparsableClass(CtClass cachedClass) {
		return cachedClass.getClassFile2() == null;
	}

	private CtClass getCachedClass(String className) {
		try {
			return getClassPool().get(className);
		} catch (NotFoundException e) {
			throw new MissingClassException("Expected to find " + className, e);
		}
	}

	private String hash(CtClass ctClass) {
		try {
			String hash = Hashing.sha256().hashBytes(ctClass.toBytecode()).toString();
			ctClass.defrost();
			return hash;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public JavaClass parse(File file) throws IOException {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			CtClass ctClass = getClassPool().makeClass(inputStream);

			String hash = hash(ctClass);
			JavaAssistClass clazz = classesByHash.get(hash);
			if (clazz == null) {
				clazz = new JavaAssistClass(ctClass);
				clazz.setClassFile(file);

				classesByHash.put(hash, clazz);
			}

			return clazz;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	@Override
	public void clear() {
		classPool = null;
	}
}
