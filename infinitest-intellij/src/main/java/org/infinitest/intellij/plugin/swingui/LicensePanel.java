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
import static java.awt.Font.*;
import static javax.swing.BorderFactory.*;
import static javax.swing.Box.*;
import static org.infinitest.util.Icon.*;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

public class LicensePanel extends JPanel
{
    private static final long serialVersionUID = -8203956850959996033L;

    private static final String INDIVIDUAL_LICENSE_TERMS = "If you are an individual, "
                    + "Improving Works grants to you, a personal, "
                    + "nonexclusive license to make and use copies of the SOFTWARE "
                    + "PRODUCT for the primary purpose of designing, creating and "
                    + "testing computer software programs (\"SOFTWARE "
                    + "DEVELOPMENT\") for which you are the owner of copyright. "
                    + "You may not use the SOFTWARE PRODUCT for any SOFTWARE "
                    + "DEVELOPMENT performed on behalf of an entity, including "
                    + "any work made for hire, whether commissioned under contract "
                    + "or as an employee within the scope of employment.";

    public LicensePanel()
    {
        super(new FlowLayout(LEFT));

        Box panels = createVerticalBox();
        panels.setPreferredSize(new Dimension(600, 600));

        panels.add(licenseTypePanel());
        panels.add(licenseStatusPanel());
        panels.add(changeLicensePanel());
        panels.add(createVerticalGlue());

        add(panels);
    }

    private JComponent licenseTypePanel()
    {
        JPanel licenseTypePanel = new JPanel(new FlowLayout(LEFT));
        licenseTypePanel.setBorder(createTitledBorder("License Type"));
        licenseTypePanel.setOpaque(false);

        JLabel licenseTypeLabel = new JLabel("30 Day Trial License");
        licenseTypeLabel.setFont(new Font("SansSerif", BOLD, 24));

        licenseTypePanel.add(new JLabel(new ImageIcon(TRIAL.getLargeFormatUrl())));
        licenseTypePanel.add(licenseTypeLabel);
        return licenseTypePanel;
    }

    private JComponent licenseStatusPanel()
    {
        JPanel licenseTypePanel = new JPanel(new FlowLayout(LEFT));
        licenseTypePanel.setBorder(createTitledBorder("Upgrade and Support Status"));
        licenseTypePanel.add(new JLabel("30 Days Remaining"));
        return licenseTypePanel;
    }

    private JComponent changeLicensePanel()
    {
        Box changeLicensePanel = createVerticalBox();
        changeLicensePanel.setBorder(createTitledBorder("Change License"));

        changeLicensePanel.add(individualLicensePanel());
        changeLicensePanel.add(separator());
        changeLicensePanel.add(commercialLicensePanel());

        return changeLicensePanel;
    }

    private JComponent individualLicensePanel()
    {
        Box freeLicensePanel = createVerticalBox();

        JLabel label = new JLabel("Choose a free license for Individual use");
        label.setFont(new Font(null, Font.BOLD, 12));
        label.setAlignmentX(CENTER_ALIGNMENT);
        freeLicensePanel.add(label);
        freeLicensePanel.add(termsPanel());

        return freeLicensePanel;
    }

    private JComponent commercialLicensePanel()
    {
        Box commercialLicensePanel = createVerticalBox();

        JLabel label = new JLabel("Enter a commercial license key for Corporate use");
        label.setFont(new Font(null, Font.BOLD, 12));
        label.setAlignmentX(CENTER_ALIGNMENT);

        commercialLicensePanel.add(label);
        commercialLicensePanel.add(licensePanel());

        return commercialLicensePanel;
    }

    private JComponent termsPanel()
    {
        Box termsPanel = createVerticalBox();

        termsPanel.add(leftAlign(new JLabel("Terms of Individual use")));

        JTextArea licenseTerms = new JTextArea();
        licenseTerms.setLineWrap(true);
        licenseTerms.setText(INDIVIDUAL_LICENSE_TERMS);
        licenseTerms.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        termsPanel.add(licenseTerms);

        termsPanel.add(rightAlign(new JButton("Accept Terms")));

        return termsPanel;
    }

    private JComponent licensePanel()
    {
        Box licensePanel = createVerticalBox();

        licensePanel.add(leftAlign(new JLabel("Enter License Key")));
        licensePanel.add(new JTextField());
        licensePanel.add(rightAlign(new JButton("Update License")));

        return licensePanel;
    }

    private JComponent separator()
    {
        Box panel = createHorizontalBox();
        panel.add(new JSeparator());
        panel.add(new JLabel(" OR "));
        panel.add(new JSeparator());
        return panel;
    }

    private JComponent leftAlign(JComponent component)
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(component);
        return panel;
    }

    private JComponent rightAlign(JComponent component)
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(component);
        return panel;
    }
}
