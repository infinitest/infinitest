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
package org.infinitest.eclipse.console;

import static org.eclipse.jface.resource.ImageDescriptor.*;

import java.io.IOException;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

// RISK Untested
public class ConsoleOutputWriter implements TextOutputWriter
{
    private MessageConsole console;

    public void activate()
    {
        getConsole().activate();
    }

    public void appendText(String newText)
    {
        try
        {
            MessageConsoleStream stream = getConsole().newMessageStream();
            stream.print(newText);
            stream.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private MessageConsole getConsole()
    {
        if (console == null)
        {
            ConsolePlugin plugin = ConsolePlugin.getDefault();
            IConsoleManager conMan = plugin.getConsoleManager();
            console = new MessageConsole("Infinitest Console", getMissingImageDescriptor());
            console.getDocument().set("");
            conMan.addConsoles(new IConsole[] { console });
        }

        return console;
    }

    public void clearConsole()
    {
        getConsole().clearConsole();
    }
}
