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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenTreeNodesAreAdded {
	private DefaultTreeModel model;
	private JTree tree;
	private MutableTreeNode root;

	@BeforeEach
	void inContext() {
		tree = new JTree();
		root = new DefaultMutableTreeNode("root");
		model = new DefaultTreeModel(root);
	}

	@Test
	void shouldExpandExistingNodes() {
		MutableTreeNode child = new DefaultMutableTreeNode("child");
		model.insertNodeInto(child, root, 0);
		tree.setModel(model);
		tree.collapseRow(0);
		assertFalse(tree.isExpanded(0));

		TreeModelExpansionListener.watchTree(tree);
		assertTrue(tree.isExpanded(0));
	}

	@Test
	void shouldExpandTreeWhenEventIsFired() {
		tree.setModel(model);
		tree.collapseRow(0);
		assertFalse(tree.isExpanded(0));

		TreeModelExpansionListener.watchTree(tree);
		MutableTreeNode child = new DefaultMutableTreeNode("child");
		model.insertNodeInto(child, root, 0);
		assertTrue(tree.isExpanded(0));
	}

}
