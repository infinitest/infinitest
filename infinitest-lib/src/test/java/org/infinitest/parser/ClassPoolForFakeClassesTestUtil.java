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

import static org.infinitest.environment.FakeEnvironments.*;

import javassist.*;

class ClassPoolForFakeClassesTestUtil {

	private ClassPool classPool = new ClassPool();

	ClassPoolForFakeClassesTestUtil() throws NotFoundException {
		classPool = new ClassPool();
		// If you get a NotFoundException here, you may need to make a copy of
		// your jgrapht jar file
		// that lives in a lib subdirectory. Not sure why a scalafied project
		// doesn't parse the
		// .classpath file correctly, but somewhere betweeen there and eclipse
		// that path
		// gets messed up.
		classPool.appendPathList(fakeClasspath().getRunnerFullClassPath());
		classPool.appendSystemPath();
	}

	String[] dependenciesOf(Class<?> dependingClass) {
		return getClass(dependingClass).getImports();
	}

	JavaAssistClass getClass(Class<?> dependingClass) {
		try {
			return new JavaAssistClass(classPool.get(dependingClass.getName()));
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	ClassPool getClassPool() {
		return classPool;
	}
}
