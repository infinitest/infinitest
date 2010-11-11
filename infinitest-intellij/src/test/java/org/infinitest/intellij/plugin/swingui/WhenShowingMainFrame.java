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
