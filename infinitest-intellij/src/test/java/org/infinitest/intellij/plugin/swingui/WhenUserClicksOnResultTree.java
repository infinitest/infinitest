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

import static org.infinitest.testrunner.TestEvent.TestState.METHOD_FAILURE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.infinitest.intellij.IntellijMockBase;
import org.infinitest.intellij.plugin.SourceNavigatorStub;
import org.infinitest.testrunner.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import junit.framework.AssertionFailedError;

class WhenUserClicksOnResultTree extends IntellijMockBase {
	private ResultClickListener clickListener;
	private List<TestEvent> paneEvents;
	private KeyListener keyListener;

	@BeforeEach
	void inContext() {
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
	void shouldShowInfoFrameOnTop() {
		assertTrue(getFrame().isAlwaysOnTop());
	}

	private EventInfoFrame getFrame() {
		return new EventInfoFrame(eventWithError());
	}

	@Test
	void shouldNotBeAbleToEditInfoFrame() {
		JTextArea textArea = getFrame().getTextArea();
		assertFalse(textArea.isEditable());
	}

	@Test
	void shouldNotPopUpOnSingleClick() {
		JTree tree = createFakeTree(eventWithError());
		simulateClickEvent(tree, 1, MouseEvent.BUTTON1);
		assertTrue(paneEvents.isEmpty());
	}
	
	@Test
	void checkRightClick() {
		JTree tree = createFakeTree(module);
		simulateClickEvent(tree, 1, MouseEvent.BUTTON3);
		
		verify(module).getName();
	}

	private void simulateClickEvent(JTree tree, int clickCount, int button) {
		clickListener.mouseClicked(new MouseEvent(tree, 0, 0, 0, 0, 0, clickCount, false, button));
	}

	private void simulateEnterKeyEvent(JTree tree, int keyCode) {
		keyListener.keyPressed(new KeyEvent(tree, 0, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED));
	}

	@Test
	void shouldPopUpInfoFrameOnTestNodeEnterKey() {
		JTree tree = createFakeTree(eventWithError());
		tree.setSelectionRow(1);
		simulateEnterKeyEvent(tree, KeyEvent.VK_ENTER);
		assertFalse(paneEvents.isEmpty());
	}

	@Test
	void shouldDoNothingWhenEnterPressedOnPointOfFailureNodes() {
		JTree tree = createFakeTree("MyPointOfFailure:31");
		simulateEnterKeyEvent(tree, KeyEvent.VK_ENTER);
		assertTrue(paneEvents.isEmpty());
	}

	@Test
	void shouldUseDefaultBehaviorForOtherKeys() {
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
			public TreePath getPathForLocation(int x, int y) {
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
		return new TestEvent(METHOD_FAILURE, "", "", "", new AssertionFailedError());
	}
}
