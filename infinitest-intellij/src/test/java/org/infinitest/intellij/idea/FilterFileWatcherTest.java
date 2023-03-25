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

import static org.infinitest.config.FileBasedInfinitestConfigurationSource.INFINITEST_FILTERS_FILE_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.infinitest.InfinitestCore;
import org.infinitest.intellij.IntellijMockBase;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.VerificationModeFactory;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;

/**
 * @author gtoison
 *
 */
class FilterFileWatcherTest extends IntellijMockBase {
	@Test
	void shouldIgnoreNonFilterFile() {
		FilterFileWatcher watcher = new FilterFileWatcher(project);
		VFileEvent event = mock(VFileEvent.class);
		
		when(event.getPath()).thenReturn("foo");
		
		watcher.after(Collections.singletonList(event));
		
		verifyNoInteractions(launcher);
	}

	@Test
	void shouldIgnoreNullFile() {
		FilterFileWatcher watcher = new FilterFileWatcher(project);
		VFileEvent event = mock(VFileEvent.class);
		
		when(event.getPath()).thenReturn(INFINITEST_FILTERS_FILE_NAME);
		
		watcher.after(Collections.singletonList(event));
		
		verifyNoInteractions(launcher);
	}

	@Test
	void shouldIgnoreFileNotInProject() {
		FilterFileWatcher watcher = new FilterFileWatcher(project);
		VFileEvent event = mock(VFileEvent.class);
		VirtualFile file = mock(VirtualFile.class);
		
		when(event.getPath()).thenReturn(INFINITEST_FILTERS_FILE_NAME);
		when(event.getFile()).thenReturn(file);
		when(projectFileIndex.isInContent(file)).thenReturn(false);
		
		watcher.after(Collections.singletonList(event));
		
		verifyNoInteractions(launcher);
	}

	@Test
	void shouldNotRunTestsWhenDisabled() {
		setupApplication(true);
		
		FilterFileWatcher watcher = new FilterFileWatcher(project);
		VFileEvent event = mock(VFileEvent.class);
		VirtualFile file = mock(VirtualFile.class);
		
		when(event.getPath()).thenReturn(INFINITEST_FILTERS_FILE_NAME);
		when(event.getFile()).thenReturn(file);
		when(projectFileIndex.isInContent(file)).thenReturn(true);
		
		watcher.after(Collections.singletonList(event));
		
		verifyNoInteractions(launcher);
	}

	@Test
	void shouldRunTestsWhenEnabled() {
		setupApplication(false);
		
		FilterFileWatcher watcher = new FilterFileWatcher(project);
		VFileEvent event = mock(VFileEvent.class);
		VirtualFile file = mock(VirtualFile.class);
		InfinitestCore core = mock(InfinitestCore.class);
		
		when(event.getPath()).thenReturn(INFINITEST_FILTERS_FILE_NAME);
		when(event.getFile()).thenReturn(file);
		when(projectFileIndex.isInContent(file)).thenReturn(true);
		when(launcher.getCore()).thenReturn(core);
		
		watcher.after(Collections.singletonList(event));
		
		verify(core, VerificationModeFactory.times(1)).filterFileWasUpdated();
	}
}
