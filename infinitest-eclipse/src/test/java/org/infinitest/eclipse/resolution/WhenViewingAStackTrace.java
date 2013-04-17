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
package org.infinitest.eclipse.resolution;

import static com.google.common.collect.Lists.*;
import static org.eclipse.swt.SWT.*;
import static org.junit.Assert.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.infinitest.eclipse.workspace.*;
import org.junit.*;

public class WhenViewingAStackTrace {
	private List list;
	private StackTraceElement element;
	private FailureViewer view;
	private FakeShell dialog;

	@Before
	public void inContext() {
		element = new StackTraceElement("class1", "method1", "file1", 0);
		view = new FailureViewer(null, "message", newArrayList(element), null);
		dialog = new FakeShell();
		view.show(dialog);
		list = (List) dialog.getChildren()[1];
	}

	@Test
	public void shouldBuildListItemsFromStackTrace() {
		assertEquals(1, list.getItemCount());
		assertEquals(element.toString(), list.getItem(0));
	}

	@Test
	public void shouldUseSingleSelection() {
		assertTrue((list.getStyle() & SWT.MULTI) == 0);
	}

	@Test
	public void shouldDisposeTheDialogWhenTheUserClicksOffTheViewer() {
		Listener[] listeners = dialog.getListeners(Deactivate);
		assertEquals(1, listeners.length);
	}

	@Test
	public void shouldRespondToKeyEvents() {
		assertEquals(1, list.getListeners(KeyDown).length);
	}

	@Test
	public void shouldShowTheNavigatorAndTakeFocus() {
		assertTrue(dialog.opened);
		assertTrue(dialog.active);
		assertTrue(dialog.packed);
		assertTrue(dialog.layout);
	}

	/**
	 * Presentation test harness
	 */
	public static void main(String... args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Fake Eclipse");
		shell.setSize(300, 200);
		shell.open();

		Throwable throwable = buildAFakeStackTraceRecursively(100);
		String msg = "Hello, this is a really long error message that is supposed to demonstrate how well we can" + " wrap long error messages. Evidently, they need to be long than what I've typed here";
		Shell dialog = new FailureViewer(shell, msg, newArrayList(throwable.getStackTrace()), new FakeResourceFinder()).show();
		while (!shell.isDisposed() && !dialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private static Throwable buildAFakeStackTraceRecursively(int i) {
		if (i == 0) {
			Throwable throwable = new Throwable();
			throwable.fillInStackTrace();
			return throwable;
		}
		return buildAFakeStackTraceRecursively(i - 1);
	}
}
