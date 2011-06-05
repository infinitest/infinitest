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
package org.infinitest.intellij.plugin.launcher;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.StatusChangeListener;
import org.infinitest.TestControl;
import org.infinitest.intellij.idea.language.IdeaInfinitestAnnotator;
import org.infinitest.intellij.plugin.swingui.InfinitestMainFrame;
import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.infinitest.intellij.plugin.swingui.ResultClickListener;

public class InfinitestBuilder
{
    private ResultCollector resultCollector;
    private InfinitestPresenter presenter;
    private InfinitestView view;
    private final InfinitestCore core;
    private TestControl testControl;

    public InfinitestBuilder(InfinitestCore core)
    {
        this.core = core;
        this.resultCollector = new ResultCollector(core);
    }

    public JPanel createPluginComponent(TestControl testControl)
    {
        this.testControl = testControl;
        JFrame frame = (JFrame) getView();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(frame.getContentPane(), BorderLayout.CENTER);

        createPresenter();

        return panel;
    }

    public InfinitestCore getCore()
    {
        return core;
    }

    public InfinitestView getView()
    {
        if (view == null)
        {
            view = InfinitestMainFrame.createFrame(resultCollector);
        }
        return view;
    }

    private InfinitestPresenter createPresenter()
    {
        if (presenter == null)
        {
            presenter = new InfinitestPresenter(resultCollector, core, getView(), testControl,
                            IdeaInfinitestAnnotator.getInstance());
        }
        return presenter;
    }

    public void startApp()
    {
        createPresenter();
        view.setVisible(true);
        createStatusTimer();
    }

    private void createStatusTimer()
    {
        javax.swing.Timer statusTimer = new javax.swing.Timer(999, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                createPresenter().updateStatus();
            }
        });
        statusTimer.start();
    }

    public void setResultCollector(ResultCollector resultCollector)
    {
        this.resultCollector = resultCollector;
    }

    public void addStatusListener(StatusChangeListener listener)
    {
        resultCollector.addStatusChangeListener(listener);
    }

    public void addResultClickListener(ResultClickListener listener)
    {
        view.addResultClickListener(listener);
    }

    public void removeStatusListener(StatusChangeListener listener)
    {
        resultCollector.removeStatusChangeListener(listener);
    }

    public void addPresenterListener(PresenterListener listener)
    {
        presenter.addPresenterListener(listener);
    }

    public void removePresenterListener(PresenterListener listener)
    {
        presenter.removePresenterListener(listener);
    }
}
