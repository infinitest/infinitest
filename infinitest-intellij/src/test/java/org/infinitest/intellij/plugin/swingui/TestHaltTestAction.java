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
package org.infinitest.intellij.plugin.swingui;

import static org.junit.Assert.*;

import javax.swing.*;

import org.infinitest.*;
import org.junit.*;

public class TestHaltTestAction {
	private boolean runTests;

	@Test
	public void shouldDisableAndEnableTests() {
		TestControl control = new TestControl() {
			public void setRunTests(boolean shouldRunTests) {
				runTests = shouldRunTests;
			}

			public boolean shouldRunTests() {
				return runTests;
			}
		};
		runTests = true;
		HaltTestAction action = new HaltTestAction(control);
		Icon startIcon = (Icon) action.getValue(Action.SMALL_ICON);
		action.actionPerformed(null);
		assertFalse(runTests);
		assertNotSame(startIcon, action.getValue(Action.SMALL_ICON));

		action.actionPerformed(null);
		assertTrue(runTests);
		assertSame(startIcon, action.getValue(Action.SMALL_ICON));
	}
}
