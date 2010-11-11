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

import static org.infinitest.util.InfinitestUtils.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.infinitest.testrunner.TestEvent;

class EventInfoFrame extends JDialog
{
    private static final long serialVersionUID = -1L;

    private final JTextArea textArea;
    private final JButton closeButton;

    public EventInfoFrame(TestEvent event)
    {
        setAlwaysOnTop(true);
        textArea = new JTextArea(stackTraceToString(event.getStackTrace()));
        textArea.setEditable(false);
        textArea.setColumns(80);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        getContentPane().add(new JScrollPane(textArea));
        Container southPanel = new JPanel(new FlowLayout());
        Action disposeAction = new AbstractAction("Close")
        {
            private static final long serialVersionUID = -1L;

            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        };
        closeButton = new JButton(disposeAction);
        southPanel.add(closeButton);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        String message = event.getMessage();
        if (message == null)
        {
            message = "";
        }
        setTitle(event.getTestName() + "." + event.getTestMethod() + "()");
        pack();
        closeButton.requestFocusInWindow();
    }

    public JButton getCloseButton()
    {
        return closeButton;
    }

    public static String stackTraceToString(StackTraceElement[] stackTrace)
    {
        if (stackTrace == null)
        {
            return "";
        }
        return listToMultilineString(Arrays.asList(stackTrace));
    }

    @Override
    protected JRootPane createRootPane()
    {
        JRootPane root = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        Action actionListener = new AbstractAction()
        {
            private static final long serialVersionUID = -1L;

            public void actionPerformed(ActionEvent actionEvent)
            {
                setVisible(false);
            }
        };
        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        root.getActionMap().put("ESCAPE", actionListener);

        return root;
    }

    public JTextArea getTextArea()
    {
        return textArea;
    }
}
