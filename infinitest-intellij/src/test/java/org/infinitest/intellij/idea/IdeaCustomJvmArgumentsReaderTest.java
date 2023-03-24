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
package org.infinitest.intellij.idea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.infinitest.environment.FileCustomJvmArgumentReader;
import org.infinitest.intellij.IntellijMockBase;
import org.infinitest.intellij.ModuleSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class IdeaCustomJvmArgumentsReaderTest extends IntellijMockBase {

	@Test
	void readCustomArguments(@TempDir File tempDir) throws IOException {
		ModuleSettings moduleSettings = mock(ModuleSettings.class);
		
		when(module.getService(ModuleSettings.class)).thenReturn(moduleSettings);
		when(moduleSettings.getRootDirectories()).thenReturn(Collections.singletonList(tempDir));
		File argsFiles = new File(tempDir, FileCustomJvmArgumentReader.INFINITEST_ARGS_FILE_NAME);
		argsFiles.createNewFile();
		Files.writeString(argsFiles.toPath(), "-Dtest.foo=bar");
		
		IdeaCustomJvmArgumentsReader reader = new IdeaCustomJvmArgumentsReader(module);
		
		
		List<String> arguments = reader.readCustomArguments();
		
		assertThat(arguments).containsExactly("-Dtest.foo=bar");
	}
}
