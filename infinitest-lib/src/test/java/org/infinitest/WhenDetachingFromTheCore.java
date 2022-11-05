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

import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.CoreDependencySupport.createCore;
import static org.infinitest.CoreDependencySupport.withNoTestsToRun;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.infinitest.changedetect.FakeChangeDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenDetachingFromTheCore {
	private int eventCount;
	private TestQueueListener listener;

	@BeforeEach
	void inContext() {
		listener = new TestQueueAdapter() {
			@Override
			public void reloading() {
				eventCount++;
			}
		};
	}

	@Test
	void shouldNoLongerRecieveEvents() {
		ControlledEventQueue eventQueue = new ControlledEventQueue();
		DefaultInfinitestCore core = createCore(new FakeChangeDetector(), withNoTestsToRun(), eventQueue);
		core.addTestQueueListener(listener);
		core.reload();
		eventQueue.flush();
		assertEquals(1, eventCount);

		core.removeTestQueueListener(listener);
		core.reload();
		eventQueue.flush();
		assertEquals(1, eventCount);
	}

	@Test
	void shouldTreatNormalizedListenersAsEquivelent() {
		EventNormalizer normalizer = new EventNormalizer(new FakeEventQueue());
		assertEquals(normalizer.testQueueNormalizer(listener), normalizer.testQueueNormalizer(listener));
		assertThat(normalizer.testQueueNormalizer(listener)).isNotEqualTo(normalizer.testQueueNormalizer(new TestQueueAdapter()));
	}
}
