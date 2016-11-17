package org.infinitest.eclipse.prefs;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

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
public class SwtColorFieldEditor extends ComboFieldEditor {

	private static final String[][] ENTRY_NAMES_AND_VALUES = {
		{"Black", String.valueOf(SWT.COLOR_BLACK)},
		{"Blue", String.valueOf(SWT.COLOR_BLUE )},
		{"Blue (Dark)", String.valueOf(SWT.COLOR_DARK_BLUE)},
		{"Cyan", String.valueOf(SWT.COLOR_CYAN)},
		{"Cyan(Dark)", String.valueOf(SWT.COLOR_DARK_CYAN)},
		{"Green", String.valueOf(SWT.COLOR_GREEN)},
		{"Green (Dark)", String.valueOf(SWT.COLOR_DARK_GREEN)},
		{"Gray", String.valueOf(SWT.COLOR_GRAY)},
		{"Gray (Dark)", String.valueOf(SWT.COLOR_DARK_GRAY)},
		{"Red", String.valueOf(SWT.COLOR_RED)},
		{"Red (Dark)", String.valueOf(SWT.COLOR_DARK_RED)},
		{"Magenta", String.valueOf(SWT.COLOR_MAGENTA)},
		{"Magenta (Dark)", String.valueOf(SWT.COLOR_DARK_MAGENTA)},
		{"Yellow", String.valueOf(SWT.COLOR_YELLOW)},
		{"Yellow (Dark)", String.valueOf(SWT.COLOR_DARK_YELLOW)},
		{"White", String.valueOf(SWT.COLOR_WHITE)},
	};

	public SwtColorFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, ENTRY_NAMES_AND_VALUES, parent);
		// TODO Auto-generated constructor stub
	}

}
