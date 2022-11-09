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

import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.testrunner.TestEvent.TestState.METHOD_FAILURE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JLabel;

import org.infinitest.intellij.IntellijMockBase;
import org.infinitest.testrunner.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.Application;

import junit.framework.AssertionFailedError;

class WhenShowingMainFrame extends IntellijMockBase {
  private FailureCellRenderer cellRenderer;
  private InfinitestMainFrame mainFrame;
  private InfinitestResultsPane resultsPane;

  @BeforeEach
  void inContext() {
	Application application = IntellijMockBase.setupApplication();
    
    InfinitestConsoleFrame consoleFrame = new InfinitestConsoleFrame(project);
    
    resultsPane = new InfinitestResultsPane();
	mainFrame = new InfinitestMainFrame(resultsPane, new InfinitestLogPane(application), consoleFrame);
    cellRenderer = (FailureCellRenderer) resultsPane.getTree().getCellRenderer();
  }

  @org.junit.jupiter.api.Test
  void shouldHaveTooltipToInformUsersAboutClickFunctionality() {
    assertEquals("Double-click test nodes to navigate to source", cellRenderer.getToolTipText());
  }

  @Test
  void shouldHaveIconToIndicatePoFNodes() {
    Object node = "PointOfFailure.java:32";
    JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false, false, 0, false);

    assertThat(treeCell.getIcon()).isEqualTo(AllIcons.General.Warning);
  }

  @Test
  void shouldHaveIconToIndicateFailingTest() {
    Object node = eventWithError();
    JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false, false, 0, false);

    assertThat(treeCell.getIcon()).isEqualTo(AllIcons.RunConfigurations.TestFailed);
  }

  private static TestEvent eventWithError() {
    return new TestEvent(METHOD_FAILURE, "", "", "", new AssertionFailedError());
  }
}
