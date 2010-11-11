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
package org.infinitest.intellij.plugin.swingui;

import static com.google.common.collect.Lists.*;

import java.util.Collection;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.infinitest.FailureListListener;
import org.infinitest.ResultCollector;
import org.infinitest.testrunner.PointOfFailure;
import org.infinitest.testrunner.TestEvent;

class TreeModelAdapter implements TreeModel, FailureListListener
{
    private final ResultCollector collector;
    private final List<TreeModelListener> listeners;

    public TreeModelAdapter(ResultCollector resultCollector)
    {
        collector = resultCollector;
        collector.addChangeListener(this);
        listeners = newArrayList();
    }

    public void addTreeModelListener(TreeModelListener l)
    {
        listeners.add(l);
    }

    public Object getChild(Object parent, int index)
    {
        if (parent == getRoot())
        {
            return collector.getPointOfFailure(index);
        }
        if (collector.isPointOfFailure(parent))
        {
            return collector.getTestsFor((PointOfFailure) parent).get(index);
        }
        return null;
    }

    public int getChildCount(Object parent)
    {
        if (parent.equals(getRoot()))
        {
            return collector.getPointOfFailureCount();
        }
        if (collector.isPointOfFailure(parent))
        {
            return collector.getTestsFor((PointOfFailure) parent).size();
        }
        return 0;
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        if (getRoot().equals(parent))
        {
            return collector.getPointOfFailureIndex((PointOfFailure) child);
        }
        return 0;
    }

    public Object getRoot()
    {
        return "";
    }

    public boolean isLeaf(Object node)
    {
        return getChildCount(node) == 0;
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
        listeners.remove(l);
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {
        // User changes are ignored
    }

    protected void fireTreeStructureChanged()
    {
        int[] childIndices = new int[0];
        Object[] children = new Object[0];
        for (TreeModelListener listener : listeners)
        {
            listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(getRoot()), childIndices, children));
        }
    }

    public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved)
    {
        fireTreeStructureChanged();
    }

    public void failuresUpdated(Collection<TestEvent> updatedFailures)
    {
        fireTreeStructureChanged();
    }
}
