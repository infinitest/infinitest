package org.infinitest.intellij.plugin.swingui;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.infinitest.testrunner.TestEvent;

public interface TestEventSelectionListener
{
    void showInfoPane(TestEvent event);

    void expandPath(JTree tree, TreePath path);
}
