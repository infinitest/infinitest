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
