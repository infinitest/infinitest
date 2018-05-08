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
package org.infinitest.testrunner.exampletests.testng;

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
