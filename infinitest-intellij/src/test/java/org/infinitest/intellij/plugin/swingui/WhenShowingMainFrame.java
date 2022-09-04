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
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.infinitest.intellij.IntellijMockBase;
import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import junit.framework.AssertionFailedError;

public class WhenShowingMainFrame extends IntellijMockBase {
  private FailureCellRenderer cellRenderer;
  private InfinitestMainFrame mainFrame;
  private InfinitestResultsPane resultsPane;
  private Component focusedComponent;

  @Before
  public void inContext() {
    IntellijMockBase.setupApplication();
    
    InfinitestConsoleFrame consoleFrame = new InfinitestConsoleFrame(project);
    
    resultsPane = new InfinitestResultsPane();
	mainFrame = new InfinitestMainFrame(resultsPane, new InfinitestLogPane(), consoleFrame);
    cellRenderer = (FailureCellRenderer) resultsPane.getTree().getCellRenderer();
  }

  @Test
  public void shouldHaveTooltipToInformUsersAboutClickFunctionality() {
    assertEquals("Double-click test nodes to navigate to source", cellRenderer.getToolTipText());
  }

  @Test
  public void shouldStartWithTreeFocused() {
    TreeFocusListener listener = new TreeFocusListener() {
      @Override
      protected void setFocus(Component c) {
        focusedComponent = c;
      }
    };
    listener.windowGainedFocus(new WindowEvent(mainFrame, 0));
    assertEquals(resultsPane.getTree(), focusedComponent);
  }

  @Test
  public void shouldHaveIconToIndicatePoFNodes() {
    Object node = "PointOfFailure.java:32";
    JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false, false, 0, false);

    assertThat(treeCell.getIcon().toString()).isEqualTo(expectedIcon("error"));
  }

  @Test
  public void shouldHaveIconToIndicateFailingTest() {
    Object node = eventWithError();
    JLabel treeCell = (JLabel) cellRenderer.getTreeCellRendererComponent(resultsPane.getTree(), node, false, false, false, 0, false);

    assertThat(treeCell.getIcon().toString()).isEqualTo(expectedIcon("failure"));
  }

  private String expectedIcon(String iconName) {
    ImageIcon expectedIcon = new ImageIcon(getClass().getResource("/org/infinitest/intellij/plugin/swingui/" + iconName + ".png"));
    return expectedIcon.toString();
  }

  private static TestEvent eventWithError() {
    return new TestEvent(METHOD_FAILURE, "", "", "", new AssertionFailedError());
  }
}
