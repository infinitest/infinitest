/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2011
 * "Matthias Droste" <matthias.droste@gmail.com>,
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
package org.infinitest.testrunner;

import static org.testng.Assert.*;

import org.testng.annotations.*;

/** Provides a set of TestNG-tests as a base to test the TestNG-configuration */
public class TestWithTestNGGroups {
	public static boolean fail;
	public static boolean dependencyFail;

	@Test
	public void hallo() {
		// left is calculated result, right is expected result
		assertEquals("actual", "actual");
	}

	@Test(groups = { "slow" })
	public void shouldNotBeTestedGroup() {
		assertFalse(fail);
	}

	@Test(groups = { "manual" })
	public void shouldNotBeTestedGroup3() {
		assertFalse(fail);
	}

	@Test(groups = { "shouldbetested" })
	public void doSomeTest() {
		long nano = System.nanoTime();
		long nano2 = System.nanoTime();
		assertTrue(nano2 >= nano);
	}

	@Test(groups = { "mixed", "slow" })
	public void shouldNotBeTestedGroup2() {
		assertFalse(fail);
	}

	@Test(groups = { "green" }, dependsOnGroups = { "slow" })
	public void shouldNoBeTestedDueToDependencyOnFilteredGroup() {
		assertFalse(dependencyFail);
	}
}
