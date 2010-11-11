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
