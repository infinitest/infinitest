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
package org.infinitest.testrunner;

import static org.infinitest.config.FileBasedInfinitestConfigurationSource.INFINITEST_FILTERS_FILE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author gtoison
 *
 */
class TestRunnerProcessTest {

	@Test
	void noFilterFile() {
		NativeRunner nativeRunner = mock(NativeRunner.class);
		new TestRunnerProcess(null, null) {
			@Override
			protected NativeRunner instantiateTestRunner(String runnerClassName) {
				return nativeRunner;
			}
		};
		
		
		verifyNoInteractions(nativeRunner);
	}

	@Test
	void filterFile(@TempDir File tempDir) throws IOException {
		File filterFile = new File(tempDir, INFINITEST_FILTERS_FILE_NAME);
		filterFile.createNewFile();
		
		NativeRunner nativeRunner = mock(NativeRunner.class);
		new TestRunnerProcess(null, filterFile) {
			@Override
			protected NativeRunner instantiateTestRunner(String runnerClassName) {
				return nativeRunner;
			}
		};
		
		
		verify(nativeRunner).setTestConfigurationSource(any());
	}
	
	@Test
	void checkIncorrectArgsCount() {
		assertThrows(IllegalArgumentException.class, () -> TestRunnerProcess.checkArgsCount(new String[4]));
	}
	
	@Test
	void checkCorrectArgsCount() {
		assertDoesNotThrow(() -> TestRunnerProcess.checkArgsCount(new String[3]));
	}
	
	@Test
	void getNullFilterFile() {
		assertNull(TestRunnerProcess.getFilterFile(new String[] {null, null, "null"}));
	}
	
	@Test
	void getFilterFile() {
		File filteFile = new File(INFINITEST_FILTERS_FILE_NAME);
		assertEquals(filteFile, TestRunnerProcess.getFilterFile(new String[] {null, null, INFINITEST_FILTERS_FILE_NAME}));
	}
}
