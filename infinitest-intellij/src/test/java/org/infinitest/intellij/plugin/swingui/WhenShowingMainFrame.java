package org.infinitest.intellij.plugin.swingui;

import static org.hamcrest.Matchers.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

public class WhenShowingMainFrame
{
    private FailureCellRenderer cellRenderer;
    private InfinitestMainFrame mainFrame;
    private InfinitestResultsPane resultsPane;
    private Component focusedComponent;

    @Before
    public void inContext()
    {
        resultsPane = new InfinitestResultsPane();
        mainFrame = new InfinitestMainFrame(resultsPane, new InfinitestLogPane());
        cellRenderer = (FailureCellRenderer) resultsPane.getTree().getCellRenderer();
    }

    @Test
    public void shouldHaveTooltipToInformUsersAboutClickFunctionality()
    {
        assertEquals("Double-click test nodes to navigate to source", cellRenderer.getToolTipText());
    }

    @Test
    public void shouldStartWithTreeFocused()
    {
        TreeFocusListener listener = new TreeFocusListener()
        {
            @Override
            protected void setFocus(Component c)
            {
                focusedComponent = c;
            }
        };
        listener.windowGainedFocus(new WindowEvent(mainFrame, 0));
        assertEquals(resultsPane.getTree(), focusedComponent);
    }

    @Test
    public void shouldHaveIconToIndicatePoFNodes()
    {
        Object node = "PointOfFailure.java:32";
        JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false,
                        false, 0, false);
        assertThat(treeCell.getIcon().toString(), is(expectedIcon("error")));
    }

    @Test
    public void shouldHaveIconToIndicateFailingTest()
    {
        Object node = eventWithError(new AssertionFailedError());
        JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false,
                        false, 0, false);
        assertThat(treeCell.getIcon().toString(), is(expectedIcon("failure")));
    }

    private String expectedIcon(String iconName)
    {
        ImageIcon expectedIcon = new ImageIcon(getClass().getResource(
                        "/org/infinitest/intellij/plugin/swingui/" + iconName + ".png"));
        return expectedIcon.toString();
    }
}
