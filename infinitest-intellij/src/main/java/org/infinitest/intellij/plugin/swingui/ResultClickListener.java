package org.infinitest.intellij.plugin.swingui;

import org.infinitest.intellij.plugin.SourceNavigator;
import org.infinitest.testrunner.TestEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResultClickListener extends MouseAdapter
{
    private SourceNavigator navigator;

    public ResultClickListener(SourceNavigator navigator)
    {
        this.navigator = navigator;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getClickCount() != 2)
            return;

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
