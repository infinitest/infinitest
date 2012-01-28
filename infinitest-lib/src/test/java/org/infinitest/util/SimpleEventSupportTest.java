/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.util;

import static org.infinitest.util.Events.*;
import static org.junit.Assert.*;

import org.junit.*;

// Reflection is OK. I promise.
// http://www.jguru.com/faq/view.jsp?EID=246569
public class SimpleEventSupportTest {
	protected boolean eventRecieved;
	private Events<SimpleListener> integerEvent;
	private SimpleListener listener;

	@Before
	public void inContext() {
		integerEvent = eventFor(SimpleListener.class);
		listener = new SimpleListener() {
			public void eventFired() {
				eventRecieved = true;
			}
		};
		integerEvent.addListener(listener);
	}

	@Test
	public void shouldFireEventsWithNoParameter() {
		integerEvent.fire();
		assertTrue(eventRecieved);
	}

	@Test
	public void canRemoveListeners() {
		integerEvent.removeListener(listener);
		integerEvent.fire();
		assertFalse(eventRecieved);
	}

	public static interface SimpleListener {
		public void eventFired();
	}
}
