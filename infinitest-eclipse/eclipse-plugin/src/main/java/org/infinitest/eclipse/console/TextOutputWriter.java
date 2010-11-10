package org.infinitest.eclipse.console;

public interface TextOutputWriter
{
    void clearConsole();

    void appendText(String newText);

    void activate();
}