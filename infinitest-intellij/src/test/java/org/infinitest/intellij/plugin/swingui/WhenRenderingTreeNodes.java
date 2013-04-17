/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
