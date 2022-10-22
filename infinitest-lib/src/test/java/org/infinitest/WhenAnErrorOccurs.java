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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.infinitest.CoreDependencySupport.createCore;
import static org.infinitest.CoreDependencySupport.withNoTestsToRun;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.infinitest.changedetect.ChangeDetector;
import org.infinitest.changedetect.FakeChangeDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenAnErrorOccurs {
	private ChangeDetector failingDetector;
	protected boolean shouldFail;
	private InfinitestCore core;

	@BeforeEach
	void inContext() {
		shouldFail = true;
		failingDetector = new FakeChangeDetector() {
			@Override
			public Set<File> findChangedFiles() throws IOException {
				if (shouldFail) {
					throw new IOException();
				}
				return Collections.emptySet();
			}
		};
		core = createCore(failingDetector, withNoTestsToRun());
	}

	@Test
	void shouldReloadIndexOnIOException() throws Exception {
		EventSupport statusSupport = new EventSupport();
		core.addTestQueueListener(statusSupport);
		core.update();
		shouldFail = false;
		core.update();
		shouldFail = true;
		core.update();
		statusSupport.assertReloadOccured();
	}

	@Test
	void shouldNotRetryIfErrorOccursTwiceInARow() {
		core.update();
		assertThatThrownBy(() -> core.update()).isInstanceOf(FatalInfinitestError.class);
	}
}
