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
package org.infinitest.eclipse.workspace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import java.net.*;

import org.infinitest.*;
import org.infinitest.eclipse.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenCreatingAndRemovingCores implements CoreLifecycleListener {
	private InfinitestCoreRegistry registry;
	private InfinitestCore coreAdded;
	private InfinitestCore coreRemoved;

	@BeforeEach
	void inContext() {
		registry = new InfinitestCoreRegistry();
	}

	@Test
	void shouldTolerateRemovingACoreThatsNotThere() throws URISyntaxException {
		assertDoesNotThrow(() -> registry.removeCore(new URI("//thisIsNotAProject")));
	}

	@Test
	void shouldFireEventWhenCoresAreCreatedOrRemoved() throws URISyntaxException {
		InfinitestCore mockCore = mock(InfinitestCore.class);
		registry.addLifecycleListener(this);
		registry.addCore(new URI("//someProject"), mockCore);
		assertSame(coreAdded, mockCore);

		registry.removeCore(new URI("//someProject"));
		assertSame(coreRemoved, mockCore);
	}
	
	@Test
	void shouldStopAllCores() throws URISyntaxException {
		URI uri1 = new URI("c:/test/1");
		URI uri2 = new URI("c:/test/2");
		
		InfinitestCore core1 = mock(InfinitestCore.class);
		InfinitestCore core2 = mock(InfinitestCore.class);
		
		registry.addCore(uri1, core1);
		registry.addCore(uri2, core2);
		
		assertThat(registry.indexedCoreCount()).isEqualTo(2);
		
		registry.stop();
		
		verify(registry.getCore(uri1)).stop();
		verify(registry.getCore(uri2)).stop();
	}

	@Override
	public void coreCreated(InfinitestCore core) {
		coreAdded = core;
	}

	@Override
	public void coreRemoved(InfinitestCore core) {
		coreRemoved = core;
	}
}
