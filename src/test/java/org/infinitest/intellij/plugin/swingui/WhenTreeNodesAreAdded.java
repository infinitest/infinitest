package org.infinitest.intellij.plugin.swingui;

import static org.junit.Assert.*;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.junit.Before;
import org.junit.Test;

public class WhenTreeNodesAreAdded
{
    private DefaultTreeModel model;
    private JTree tree;
    private MutableTreeNode root;

    @Before
    public void inContext()
    {
        tree = new JTree();
        root = new DefaultMutableTreeNode("root");
        model = new DefaultTreeModel(root);
    }

    @Test
    public void shouldExpandExistingNodes()
    {
        MutableTreeNode child = new DefaultMutableTreeNode("child");
        model.insertNodeInto(child, root, 0);
        tree.setModel(model);
        tree.collapseRow(0);
        assertFalse(tree.isExpanded(0));

        TreeModelExpansionListener.watchTree(tree);
        assertTrue(tree.isExpanded(0));
    }

    @Test
    public void shouldExpandTreeWhenEventIsFired()
    {
        tree.setModel(model);
        tree.collapseRow(0);
        assertFalse(tree.isExpanded(0));

        TreeModelExpansionListener.watchTree(tree);
        MutableTreeNode child = new DefaultMutableTreeNode("child");
        model.insertNodeInto(child, root, 0);
        assertTrue(tree.isExpanded(0));
    }

}
