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
