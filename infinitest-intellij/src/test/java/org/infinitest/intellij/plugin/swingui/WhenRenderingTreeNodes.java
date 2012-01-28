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

import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;

import javax.swing.*;

import org.infinitest.testrunner.*;
import org.junit.*;

public class WhenRenderingTreeNodes {
	private FailureCellRenderer cellRenderer;
	private InfinitestResultsPane resultsPane;

	@Before
	public void inContext() {
		resultsPane = new InfinitestResultsPane();
		cellRenderer = (FailureCellRenderer) resultsPane.getTree().getCellRenderer();
	}

	@Test
	public void shouldHaveTooltipToInformUsersAboutClickFunctionality() {
		assertEquals("Double-click test nodes to navigate to source", cellRenderer.getToolTipText());
	}

	@Test
	public void shouldHaveIconToIndicatePointOfFailureNodes() {
		Object node = "PointOfFailure.java:32";
		JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false, false, 0, false);
		assertEquals(expectedIcon("error"), treeCell.getIcon().toString());
	}

	@Test
	public void shouldHaveIconToIndicateTestNodes() {
		Object node = withATest();
		JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false, false, 0, false);
		assertEquals(expectedIcon("failure"), treeCell.getIcon().toString());
	}

	private String expectedIcon(String iconName) {
		return new ImageIcon(getClass().getResource("/org/infinitest/intellij/plugin/swingui/" + iconName + ".png")).toString();
	}

	private static TestEvent withATest() {
		return new TestEvent(TEST_CASE_STARTING, "", "", "", null);
	}
}
