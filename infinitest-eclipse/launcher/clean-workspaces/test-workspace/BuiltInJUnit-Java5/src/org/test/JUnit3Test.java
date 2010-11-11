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
package org.test;

import junit.framework.TestCase;

public class JUnit3Test extends TestCase {
	
	public void testThatShouldFailInExternalClass()
	{
		// TODO Uncommenting this should cause the failure marker to occur on this line, not in ArrayList
		//new java.util.ArrayList<Object>(null);
	}
	
	public void testThatFailsInAnInnerClass() throws Exception {
		Runnable runnable = new Runnable()
		{
			public void run() {
				// TODO Uncommenting this will cause a marker to be placed on the runnable.run()
				// Ideally, however, we'd like it to be on the fail().
				//org.junit.Assert.fail();
			}
		};
		runnable.run();
	}
}
