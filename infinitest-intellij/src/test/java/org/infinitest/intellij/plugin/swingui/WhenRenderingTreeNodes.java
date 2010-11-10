package org.infinitest.intellij.plugin.swingui;

import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.junit.Before;
import org.junit.Test;

public class WhenRenderingTreeNodes
{
    private FailureCellRenderer cellRenderer;
    private InfinitestResultsPane resultsPane;

    @Before
    public void inContext()
    {
        resultsPane = new InfinitestResultsPane();
        cellRenderer = (FailureCellRenderer) resultsPane.getTree().getCellRenderer();
    }

    @Test
    public void shouldHaveTooltipToInformUsersAboutClickFunctionality()
    {
        assertEquals("Double-click test nodes to navigate to source", cellRenderer.getToolTipText());
    }

    @Test
    public void shouldHaveIconToIndicatePointOfFailureNodes()
    {
        Object node = "PointOfFailure.java:32";
        JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false,
                        false, 0, false);
        assertEquals(expectedIcon("error"), treeCell.getIcon().toString());
    }

    @Test
    public void shouldHaveIconToIndicateTestNodes()
    {
        Object node = withATest();
        JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false,
                        false, 0, false);
        assertEquals(expectedIcon("failure"), treeCell.getIcon().toString());
    }

    private String expectedIcon(String iconName)
    {
        return new ImageIcon(getClass().getResource("/org/infinitest/intellij/plugin/swingui/" + iconName + ".png"))
                        .toString();
    }
}
