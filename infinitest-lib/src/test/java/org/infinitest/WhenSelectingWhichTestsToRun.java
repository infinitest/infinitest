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
package org.infinitest;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.environment.FakeEnvironments.fakeBuildPaths;
import static org.infinitest.environment.FakeEnvironments.fakeClasspath;
import static org.infinitest.util.InfinitestTestUtils.getFileForClass;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.List;

import org.infinitest.filter.TestFilter;
import org.infinitest.parser.ClassFileTestDetector;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.TestFakeProduct;

class WhenSelectingWhichTestsToRun {
	@Test
	void shouldIgnoreTestsInDependentProjects() {
		List<File> outputDirs = asList(new File("target/classes"));
		List<File> classDirsInClasspath = fakeBuildPaths();
		String rawClasspath = fakeClasspath().getRunnerFullClassPath();
		StandaloneClasspath classpath = new StandaloneClasspath(outputDirs, classDirsInClasspath, rawClasspath);

		ClassFileTestDetector testDetector = new ClassFileTestDetector(mock(TestFilter.class));
		testDetector.setClasspathProvider(classpath);
		File classFileNotInOutputDirectory = getFileForClass(TestFakeProduct.class);
		assertThat(testDetector.findTestsToRun(asList(classFileNotInOutputDirectory))).isEmpty();
	}
}
