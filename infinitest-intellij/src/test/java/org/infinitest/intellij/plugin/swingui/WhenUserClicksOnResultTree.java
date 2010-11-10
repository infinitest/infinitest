package org.infinitest.intellij.plugin.swingui;

import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import junit.framework.AssertionFailedError;

import org.infinitest.intellij.plugin.SourceNavigatorStub;
import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class WhenUserClicksOnResultTree
{
    private ResultClickListener clickListener;
    private List<TestEvent> paneEvents;
    private KeyListener keyListener;

    @Before
    public void inContext()
    {
        paneEvents = new ArrayList<TestEvent>();
        keyListener = new EnterPressListener()
        {
            @Override
            protected void showInfoPane(TestEvent event)
            {
                paneEvents.add(event);
            }
        };
        clickListener = new ResultClickListener(new SourceNavigatorStub());
    }

    @Test
    public void shouldShowInfoFrameOnTop()
    {
        assertTrue(getFrame().isAlwaysOnTop());
    }

    private EventInfoFrame getFrame()
    {
        return new EventInfoFrame(eventWithError(new AssertionFailedError()));
    }

    @Test
    public void shouldNotBeAbleToEditInfoFrame()
    {
        JTextArea textArea = getFrame().getTextArea();
        assertFalse(textArea.isEditable());
    }

    @Test
    public void shouldNotPopUpOnSingleClick()
    {
        JTree tree = createFakeTree(eventWithError(new AssertionFailedError()));
        simulateClickEvent(tree, 1);
        assertTrue(paneEvents.isEmpty());
    }

    private void simulateClickEvent(JTree tree, int clickCount)
    {
        clickListener.mouseClicked(new MouseEvent(tree, 0, 0, 0, 0, 0, clickCount, false));
    }

    private void simulateEnterKeyEvent(JTree tree, int keyCode)
    {
        keyListener.keyPressed(new KeyEvent(tree, 0, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED));
    }

    @Test
    public void shouldPopUpInfoFrameOnTestNodeEnterKey()
    {
        JTree tree = createFakeTree(eventWithError(new AssertionFailedError()));
        tree.setSelectionRow(1);
        simulateEnterKeyEvent(tree, KeyEvent.VK_ENTER);
        assertFalse(paneEvents.isEmpty());
    }

    @Test
    public void shouldDoNothingWhenEnterPressedOnPointOfFailureNodes()
    {
        JTree tree = createFakeTree("MyPointOfFailure:31");
        simulateEnterKeyEvent(tree, KeyEvent.VK_ENTER);
        assertTrue(paneEvents.isEmpty());
    }

    @Test
    public void shouldUseDefaultBehaviorForOtherKeys()
    {
        JTree tree = createFakeTree(eventWithError(new AssertionFailedError()));
        simulateEnterKeyEvent(tree, KeyEvent.VK_LEFT);
        assertTrue(paneEvents.isEmpty());
    }

    private JTree createFakeTree(final Object treeNode)
    {
        return new JTree()
        {
            private static final long serialVersionUID = -1L;

            @Override
            public TreePath getClosestPathForLocation(int x, int y)
            {
                return new TreePath(new Object[] { "root", "parent", treeNode });
            }

            @Override
            public TreePath getSelectionPath()
            {
                return new TreePath(new Object[] { "root", "parent", treeNode });
            }

            @Override
            public void expandPath(TreePath path)
            {
            }
        };
    }
}
