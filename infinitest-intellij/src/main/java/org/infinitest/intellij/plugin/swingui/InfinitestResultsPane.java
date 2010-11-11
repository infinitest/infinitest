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

import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.tree.TreeModel;

public class InfinitestResultsPane extends JPanel
{
    private static final long serialVersionUID = -1L;
    private static final int RESIZE_WINDOW_HANDLE_WIDTH = 13;

    private final JToolBar toolbar;
    private final ColorAnimator animator;
    private final CustomProgressBar progressBar;
    private final JTree tree;
    private Color color;

    InfinitestResultsPane()
    {
        setLayout(new BorderLayout());
        toolbar = createToolBar();
        animator = new ColorAnimator();
        progressBar = createProgressBar();
        tree = createTree();

        JPanel southComponent = new JPanel(new BorderLayout());
        if (UIManager.getSystemLookAndFeelClassName().equals("apple.laf.AquaLookAndFeel"))
        {
            southComponent.add(Box.createHorizontalStrut(RESIZE_WINDOW_HANDLE_WIDTH), BorderLayout.EAST);
        }
        southComponent.add(progressBar, BorderLayout.CENTER);
        southComponent.add(toolbar, BorderLayout.WEST);
        add(southComponent, BorderLayout.SOUTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    private static JToolBar createToolBar()
    {
        JToolBar toolbar = new JToolBar(SwingConstants.HORIZONTAL);
        toolbar.setName("TOOLBAR");
        return toolbar;
    }

    private static JTree createTree()
    {
        JTree resultsTree = new JTree();
        resultsTree.setName("TREEVIEW");
        resultsTree.setRootVisible(false);
        resultsTree.setEditable(false);
        resultsTree.setShowsRootHandles(true);
        resultsTree.setCellRenderer(new FailureCellRenderer());
        ToolTipManager tipManager = ToolTipManager.sharedInstance();
        tipManager.registerComponent(resultsTree);
        resultsTree.addKeyListener(new EnterPressListener());
        return resultsTree;
    }

    private CustomProgressBar createProgressBar()
    {
        final CustomProgressBar progress = new CustomProgressBar();
        progress.setName("PROGRESSBAR");
        color = UNKNOWN_COLOR;
        Timer timer = new Timer(animator.getAnimationRate(), new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                progress.setForeground(animator.shiftColorOnAnimationTick(color));
                progress.repaint();
                animator.tick();
            }
        });
        timer.start();
        return progress;
    }

    private JProgressBar getProgressBar()
    {
        return progressBar;
    }

    private JToolBar getToolbar()
    {
        return toolbar;
    }

    public JTree getTree()
    {
        return tree;
    }

    public void setAngerBasedOnTime(long timeSinceGreen)
    {
        animator.setAngerBasedOnTime(timeSinceGreen);
    }

    public int getAngerLevel()
    {
        return animator.getAngerLevel();
    }

    public void setAngerLevel(int anger)
    {
        animator.setAngerLevel(anger);
    }

    public void setProgressBarColor(Color progressBarColor)
    {
        color = progressBarColor;
    }

    public void setProgress(int progress)
    {
        getProgressBar().setValue(progress);
    }

    public void setMaximumProgress(int maxProgress)
    {
        getProgressBar().setMaximum(maxProgress);
    }

    public int getMaximumProgress()
    {
        return getProgressBar().getMaximum();
    }

    public int getProgress()
    {
        return getProgressBar().getValue();
    }

    public void setCurrentTest(String testName)
    {
        progressBar.setCurrentTest(testName);
    }

    public void addAction(Action action)
    {
        getToolbar().add(action);
    }

    public void setResultsModel(TreeModel results)
    {
        getTree().setModel(results);
        TreeModelExpansionListener.watchTree(getTree());
    }

    public Color getProgressBarColor()
    {
        return color;
    }

    public void setStatusMessage(String statusMessage)
    {
        progressBar.setStatusMessage(statusMessage);
    }

    public void addResultClickListener(ResultClickListener listener)
    {
        tree.addMouseListener(listener);
    }
}
