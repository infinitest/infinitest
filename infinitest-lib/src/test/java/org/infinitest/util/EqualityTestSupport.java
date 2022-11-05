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
package org.infinitest.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

public abstract class EqualityTestSupport {
	protected abstract Object createEqualInstance();

	protected abstract Object createUnequalInstance();

	protected List<Object> createUnequalInstances() {
		List<Object> list = new ArrayList<Object>();
		list.add(createUnequalInstance());
		return list;
	}

	@Test
	void identicalObjectsShouldBeEqual() {
		Object reference = createEqualInstance();
		assertEquals(reference, reference);
	}

	@Test
	void unequalInstancesAreUnique() {
		List<Object> unequalInstances = createUnequalInstances();
		HashSet<Object> set = new HashSet<Object>(unequalInstances);
		assertEquals(set.size(), unequalInstances.size());
	}

	@Test
	void differentObjectsShouldBeUnequal() {
		Object equal = createEqualInstance();
		for (Object other : createUnequalInstances()) {
			assertNotEquals(equal, other);
		}
	}

	@Test
	void equalObjectsShouldHaveSameHashcode() {
		assertEquals(createEqualInstance().hashCode(), createEqualInstance().hashCode());
	}

	@Test
	void shouldDifferentTypesShouldNotBeEqual() {
		assertNotEquals(createEqualInstance(), new Object());
	}
}
