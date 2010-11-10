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
