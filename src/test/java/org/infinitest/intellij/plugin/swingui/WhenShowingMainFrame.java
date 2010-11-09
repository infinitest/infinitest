package org.infinitest.intellij.plugin.swingui;

import junit.framework.AssertionFailedError;
import static org.hamcrest.Matchers.is;
import static org.infinitest.util.EventFakeSupport.eventWithError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.event.WindowEvent;

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
        ImageIcon expectedIcon = new ImageIcon(getClass().getResource("/org/infinitest/intellij/plugin/swingui/"
                + iconName + ".png"));
        return expectedIcon.toString();
    }
}
