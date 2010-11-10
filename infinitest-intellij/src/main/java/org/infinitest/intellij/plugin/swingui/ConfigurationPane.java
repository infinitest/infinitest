package org.infinitest.intellij.plugin.swingui;

import javax.swing.Box;
import static javax.swing.Box.createHorizontalBox;
import static javax.swing.Box.createHorizontalStrut;
import static javax.swing.Box.createVerticalBox;
import static javax.swing.Box.createVerticalStrut;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import static java.awt.BorderLayout.CENTER;
import java.awt.Component;
import java.awt.FlowLayout;
import static java.awt.FlowLayout.LEFT;
import java.awt.Font;

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
        add(pane, CENTER);
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
