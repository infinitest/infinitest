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

import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.infinitest.TestControl;
import org.infinitest.intellij.plugin.*;
import org.infinitest.testrunner.*;

import com.intellij.openapi.module.Module;

public class ResultClickListener extends MouseAdapter {
	private final SourceNavigator navigator;

	public ResultClickListener(SourceNavigator navigator) {
		this.navigator = navigator;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JTree tree = (JTree) e.getSource();
		if (e.getClickCount() == 2) {
			mouseDoubleClicked(tree, e);
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			mouseRightClicked(tree, e);
		}
	}

	private void mouseDoubleClicked(JTree tree, MouseEvent e) {
		TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
		Object treeNode = path.getLastPathComponent();
		if (treeNode instanceof TestEvent) {
			TestEvent event = (TestEvent) treeNode;
			navigator.open(classFor(event)).line(lineFor(event));
		}
	}
	
	private void mouseRightClicked(JTree tree, MouseEvent e) {
		TreePath path = tree.getPathForLocation(e.getX(), e.getY());
		if (path != null && path.getLastPathComponent() instanceof Module) {
			Module module = (Module) path.getLastPathComponent();
			
			TestControl testControl = module.getService(TestControl.class);
			
			JPopupMenu popupMenu = new JPopupMenu();
			JCheckBoxMenuItem testControlMenuItem = new JCheckBoxMenuItem("Enable/disable tests", testControl.shouldRunTests());
			testControlMenuItem.addActionListener(x -> toggleTestControl(testControl, testControlMenuItem, tree));
			popupMenu.add(testControlMenuItem);
			
			if (tree.isShowing()) {
				popupMenu.show(tree, e.getX(), e.getY());
			}
		}
	}

	private void toggleTestControl(TestControl testControl, JCheckBoxMenuItem testControlMenuItem, JTree tree) {
		testControl.setRunTests(testControlMenuItem.isSelected());
		
		if (!testControl.shouldRunTests()) {
			((TreeModelAdapter) tree.getModel()).fireTreeStructureChanged();
		}
	}

	private int lineFor(TestEvent event) {
		return event.getPointOfFailure().getLineNumber();
	}

	private String classFor(TestEvent event) {
		return event.getPointOfFailure().getClassName();
	}
}
