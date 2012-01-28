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
package com.fakeco.fakeproduct;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import com.fakeco.fakeproduct.id.*;

@ClassAnnotation
public class FakeProduct {
	@SuppressWarnings("unused") @FieldAnnotation private FakeId id;

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
