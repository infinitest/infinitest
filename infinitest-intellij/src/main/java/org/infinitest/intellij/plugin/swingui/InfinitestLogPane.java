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
