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
package com.fakeco.fakeproduct;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import com.fakeco.fakeproduct.id.*;

@ClassAnnotation
public class FakeProduct {
	@FieldAnnotation private FakeId id;

	public FakeProduct() {
		id = new FakeId();
		// LogFactory.getLog(getClass()).warn("Warning");
	}

	public FakeProduct(FakeId id) {
		id.hashCode();
	}

	public int getSeven() {
		return 7;
	}

	@MethodAnnotation
	public void createInner() {
		StaticInnerClass clazz = new StaticInnerClass();
		clazz.actionPerformed(null);
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused") List<Object> list;

		System.out.println("Hello World");
		FakeEnum fakeEnum = FakeEnum.ONE;
		fakeEnum.name();
	}

	public class TestNothing {
		public void testThisTestIsNotAUnitTestAndItShouldntBeRun() {
			throw new IllegalStateException();
		}
	}

	@SuppressWarnings("all")
	private static class StaticInnerClass extends AbstractAction {
		private StaticInnerClass() {
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println("Hello World");
		}
	}
}
