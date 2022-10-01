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
package org.infinitest.intellij;

import javax.swing.Icon;

import org.infinitest.CoreStatus;

import com.intellij.openapi.util.IconLoader;

public final class InfinitestIcons {

	public static final Icon WAITING = IconLoader.getIcon("/infinitest-waiting.png", InfinitestIcons.class);
	public static final Icon RUNNING = IconLoader.getIcon("/infinitest.png", InfinitestIcons.class);
	public static final Icon SUCCESS = IconLoader.getIcon("/infinitest-success.png", InfinitestIcons.class);
	public static final Icon FAILURE = IconLoader.getIcon("/infinitest-failure.png", InfinitestIcons.class);

	private InfinitestIcons() {
	}
	
	public static Icon getIcon(CoreStatus status) {
		switch (status) {
		case FAILING:
			return FAILURE;
		case INDEXING:
		case SCANNING:
		case RUNNING:
			return RUNNING;
		case PASSING:
			return SUCCESS;
		default:
			return FAILURE;
		}
	}
}
