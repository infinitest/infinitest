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

import java.awt.Color;
import java.awt.Component;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.tree.TreeModel;

import org.infinitest.ResultCollector;

public class InfinitestMainFrame extends JFrame implements InfinitestView
{
    private static final long serialVersionUID = -1L;
    private static final String APP_TITLE = "Infinitest";

    private final InfinitestResultsPane resultsPane;
    private final InfinitestLogPane logPane;

    public InfinitestMainFrame()
    {
        this(new InfinitestResultsPane(), new InfinitestLogPane());
    }

    InfinitestMainFrame(InfinitestResultsPane resultsPane, InfinitestLogPane logPane)
    {
        this.resultsPane = resultsPane;
        this.logPane = logPane;
        initializeFrame();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Results", resultsPane);
        tabbedPane.add("Logging", logPane);
        add(tabbedPane);
    }

    public static InfinitestView createFrame(ResultCollector results)
    {
        InfinitestMainFrame mainFrame = new InfinitestMainFrame();
        mainFrame.setResultsModel(new TreeModelAdapter(results));
        return mainFrame;
    }

    private void initializeFrame()
    {
        URL iconURL = InfinitestMainFrame.class.getResource("infinitest-icon.png");
        setIconImage(new ImageIcon(iconURL).getImage());
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowFocusListener(new TreeFocusListener());
    }

    public void setAngerBasedOnTime(long timeSinceGreen)
    {
        resultsPane.setAngerBasedOnTime(timeSinceGreen);
    }

    public void setProgress(int progress)
    {
        resultsPane.setProgress(progress);
    }

    public int getProgress()
    {
        return resultsPane.getProgress();
    }

    public void setProgressBarColor(Color color)
    {
        resultsPane.setProgressBarColor(color);
    }

    public Color getProgressBarColor()
    {
        return resultsPane.getProgressBarColor();
    }

    public void setMaximumProgress(int maxProgress)
    {
        resultsPane.setMaximumProgress(maxProgress);
    }

    public int getMaximumProgress()
    {
        return resultsPane.getMaximumProgress();
    }

    public void setCycleTime(String timeStamp)
    {
        setTitle(APP_TITLE + " - Cycle Time: " + timeStamp);
    }

    public void setCurrentTest(String testName)
    {
        resultsPane.setCurrentTest(testName);
    }

    public void addAction(Action action)
    {
        resultsPane.addAction(action);
    }

    public void setResultsModel(TreeModel results)
    {
        resultsPane.setResultsModel(results);
    }

    public void setStatusMessage(String message)
    {
        resultsPane.setStatusMessage(message);
    }

    public void writeLogMessage(String message)
    {
        logPane.writeMessage(message);
    }

    public void writeError(String message)
    {
        // nothing to do here
    }

    public void addResultClickListener(ResultClickListener listener)
    {
        resultsPane.addResultClickListener(listener);
    }

    public int getAngerLevel()
    {
        return resultsPane.getAngerLevel();
    }

    public void setAngerLevel(int anger)
    {
        resultsPane.setAngerLevel(anger);
    }

    public Component getTree()
    {
        return resultsPane.getTree();
    }
}
