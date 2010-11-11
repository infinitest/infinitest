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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.infinitest.intellij.plugin.SourceNavigator;
import org.infinitest.testrunner.TestEvent;

public class ResultClickListener extends MouseAdapter
{
    private final SourceNavigator navigator;

    public ResultClickListener(SourceNavigator navigator)
    {
        this.navigator = navigator;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getClickCount() != 2)
        {
            return;
        }

        JTree tree = (JTree) e.getSource();
        TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
        Object treeNode = path.getLastPathComponent();
        if (treeNode instanceof TestEvent)
        {
            TestEvent event = (TestEvent) treeNode;
            navigator.open(classFor(event)).line(lineFor(event));
        }
    }

    private int lineFor(TestEvent event)
    {
        return event.getPointOfFailure().getLineNumber();
    }

    private String classFor(TestEvent event)
    {
        return event.getPointOfFailure().getClassName();
    }
}
