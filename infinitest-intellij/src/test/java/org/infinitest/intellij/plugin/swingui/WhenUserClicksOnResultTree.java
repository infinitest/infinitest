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
package org.infinitest.intellij.plugin.swingui;

import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.infinitest.intellij.plugin.*;
import org.infinitest.testrunner.*;
import org.junit.*;
import org.junit.Test;

public class WhenUserClicksOnResultTree {
	private ResultClickListener clickListener;
	private List<TestEvent> paneEvents;
	private KeyListener keyListener;

	@Before
	public void inContext() {
		paneEvents = new ArrayList<TestEvent>();
		keyListener = new EnterPressListener() {
			@Override
			protected void showInfoPane(TestEvent event) {
				paneEvents.add(event);
			}
		};
		clickListener = new ResultClickListener(new SourceNavigatorStub());
	}

	@Test
	public void shouldShowInfoFrameOnTop() {
		assertTrue(getFrame().isAlwaysOnTop());
	}

	private EventInfoFrame getFrame() {
		return new EventInfoFrame(eventWithError());
	}

	@Test
	public void shouldNotBeAbleToEditInfoFrame() {
		JTextArea textArea = getFrame().getTextArea();
		assertFalse(textArea.isEditable());
	}

	@Test
	public void shouldNotPopUpOnSingleClick() {
		JTree tree = createFakeTree(eventWithError());
		simulateClickEvent(tree, 1);
		assertTrue(paneEvents.isEmpty());
	}

	private void simulateClickEvent(JTree tree, int clickCount) {
		clickListener.mouseClicked(new MouseEvent(tree, 0, 0, 0, 0, 0, clickCount, false));
	}

	private void simulateEnterKeyEvent(JTree tree, int keyCode) {
		keyListener.keyPressed(new KeyEvent(tree, 0, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED));
	}

	@Test
	public void shouldPopUpInfoFrameOnTestNodeEnterKey() {
		JTree tree = createFakeTree(eventWithError());
		tree.setSelectionRow(1);
		simulateEnterKeyEvent(tree, KeyEvent.VK_ENTER);
		assertFalse(paneEvents.isEmpty());
	}

	@Test
	public void shouldDoNothingWhenEnterPressedOnPointOfFailureNodes() {
		JTree tree = createFakeTree("MyPointOfFailure:31");
		simulateEnterKeyEvent(tree, KeyEvent.VK_ENTER);
		assertTrue(paneEvents.isEmpty());
	}

	@Test
	public void shouldUseDefaultBehaviorForOtherKeys() {
		JTree tree = createFakeTree(eventWithError());
		simulateEnterKeyEvent(tree, KeyEvent.VK_LEFT);
		assertTrue(paneEvents.isEmpty());
	}

	private JTree createFakeTree(final Object treeNode) {
		return new JTree() {
			private static final long serialVersionUID = -1L;

			@Override
			public TreePath getClosestPathForLocation(int x, int y) {
				return new TreePath(new Object[] { "root", "parent", treeNode });
			}

			@Override
			public TreePath getSelectionPath() {
				return new TreePath(new Object[] { "root", "parent", treeNode });
			}

			@Override
			public void expandPath(TreePath path) {
			}
		};
	}

	private static TestEvent eventWithError() {
		return new TestEvent(METHOD_FAILURE, "", "", "", new AssertionError());
	}
}
