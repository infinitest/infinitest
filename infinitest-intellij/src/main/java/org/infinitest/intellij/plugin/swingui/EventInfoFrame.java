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
