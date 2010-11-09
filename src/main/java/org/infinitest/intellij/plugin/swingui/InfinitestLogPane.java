package org.infinitest.intellij.plugin.swingui;

import static java.awt.BorderLayout.*;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class InfinitestLogPane extends JPanel
{
    private static final long serialVersionUID = -1L;
    private static final String CRLF = System.getProperty("line.separator");

    private final JTextArea textArea;

    public InfinitestLogPane()
    {
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), CENTER);
    }

    public void writeMessage(final String message)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                textArea.append(message);
                textArea.append(CRLF);
            }
        });
    }

    public void writeError(String message, Exception error)
    {
        writeMessage(message);
        writeMessage(stackTraceFor(error));
    }

    private String stackTraceFor(Exception error)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(output);
        error.printStackTrace(writer);
        writer.close();

        return output.toString();
    }
}
