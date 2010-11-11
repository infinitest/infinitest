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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author <a href="mailto:benrady@gmail.com".Ben Rady</a>
 */
class FailureCellRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = -1L;

    public FailureCellRenderer()
    {
        setToolTipText("Double-click test nodes to navigate to source");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                    boolean leaf, int row, boolean focused)
    {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focused);
        label.setIcon(loadIcon(value));
        return label;
    }

    private Icon loadIcon(Object node)
    {
        if (node instanceof String)
        {
            return new ImageIcon(getClass().getResource("/org/infinitest/intellij/plugin/swingui/error.png"));
        }
        return new ImageIcon(getClass().getResource("/org/infinitest/intellij/plugin/swingui/failure.png"));
    }

}
