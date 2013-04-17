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

import static com.google.common.collect.Lists.*;

import java.util.*;

import javax.swing.event.*;
import javax.swing.tree.*;

import org.infinitest.*;
import org.infinitest.testrunner.*;

class TreeModelAdapter implements TreeModel, FailureListListener {
	private final ResultCollector collector;
	private final List<TreeModelListener> listeners;

	public TreeModelAdapter(ResultCollector resultCollector) {
		collector = resultCollector;
		collector.addChangeListener(this);
		listeners = newArrayList();
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent == getRoot()) {
			return collector.getPointOfFailure(index);
		}
		if (collector.isPointOfFailure(parent)) {
			return collector.getTestsFor((PointOfFailure) parent).get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent.equals(getRoot())) {
			return collector.getPointOfFailureCount();
		}
		if (collector.isPointOfFailure(parent)) {
			return collector.getTestsFor((PointOfFailure) parent).size();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (getRoot().equals(parent)) {
			return collector.getPointOfFailureIndex((PointOfFailure) child);
		}
		return 0;
	}

	@Override
	public Object getRoot() {
		return "";
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// User changes are ignored
	}

	protected void fireTreeStructureChanged() {
		int[] childIndices = new int[0];
		Object[] children = new Object[0];
		for (TreeModelListener listener : listeners) {
			listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(getRoot()), childIndices, children));
		}
	}

	@Override
	public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved) {
		fireTreeStructureChanged();
	}

	@Override
	public void failuresUpdated(Collection<TestEvent> updatedFailures) {
		fireTreeStructureChanged();
	}
}
