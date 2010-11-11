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

import static java.awt.FlowLayout.*;
import static javax.swing.Box.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

public class ConfigurationPane extends JPanel
{
    private static final long serialVersionUID = -1L;
    private JRadioButton enableScmButton;

    public ConfigurationPane()
    {
        setLayout(new BorderLayout());

        JTabbedPane pane = new JTabbedPane();
        pane.addTab("General", createGeneralTab());
        pane.addTab("Licensing", new LicensePanel());
        add(pane, BorderLayout.CENTER);
    }

    private Component createGeneralTab()
    {
        JPanel panel = new JPanel(new FlowLayout(LEFT));

        Box box = createHorizontalBox();

        box.add(logo());
        box.add(createHorizontalStrut(20));
        box.add(scmUpdatePane());

        panel.add(box);

        return panel;
    }

    private JComponent logo()
    {
        ImageIcon logo = new ImageIcon(getClass().getResource("/infinitestMed.png"));
        return new JLabel(logo);
    }

    private JComponent scmUpdatePane()
    {
        Box box = createVerticalBox();

        JLabel heading = new JLabel("Green Hooks");
        heading.setFont(new Font(null, Font.BOLD, 14));

        box.add(heading);
        box.add(createVerticalStrut(10));
        box.add(new JLabel("Automatically perform SCM update when tests pass?"));
        box.add(optionsBox());

        return box;
    }

    private Box optionsBox()
    {
        ButtonGroup options = new ButtonGroup();

        enableScmButton = new JRadioButton("Yes, please");
        options.add(enableScmButton);

        JRadioButton noButton = new JRadioButton("No, thanks");
        noButton.setSelected(true);
        options.add(noButton);

        Box optionsBox = createVerticalBox();
        optionsBox.add(enableScmButton);
        optionsBox.add(noButton);

        return optionsBox;
    }

    public boolean isScmUpdateEnabled()
    {
        return enableScmButton.isSelected();
    }

    public void setScmUpdateEnabled(boolean scmUpdateEnabled)
    {
        enableScmButton.setSelected(scmUpdateEnabled);
    }
}
