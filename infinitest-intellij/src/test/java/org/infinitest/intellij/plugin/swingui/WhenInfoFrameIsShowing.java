package org.infinitest.intellij.plugin.swingui;

import static com.google.common.collect.Lists.*;
import static org.infinitest.intellij.plugin.swingui.EventInfoFrame.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.swing.JFrame;

import org.junit.Test;

public class WhenInfoFrameIsShowing
{
    @Test
    public void shouldCloseWithEscapeKey()
    {
        EventInfoFrame frame = new EventInfoFrame(withATest());
        assertEquals(frame.getRootPane().getActionMap().keys()[0], "ESCAPE");
    }

    @Test
    public void shouldReturnEmptyStringForNullStackTrace()
    {
        assertEquals("", stackTraceToString(null));
    }

    @Test
    public void shouldLimitStackTraceStringsTo50Lines()
    {
        List<StackTraceElement> elements = newArrayList();
        for (int i = 0; i < 100; i++)
        {
            elements.add(new StackTraceElement("class", "method", "file", 0));
        }
        StackTraceElement[] traceElements = elements.toArray(new StackTraceElement[0]);
        String[] lines = stackTraceToString(traceElements).split("\\n");
        assertEquals(51, lines.length);
        assertEquals("50 more...", lines[50]);
    }

    public static void main(String[] args)
    {
        AssertionError assertionError = new AssertionError(
                        "This is a very long error message. Who would type such a message? It's crazy. This is much too long. This must be stopped. It cannot be allowed to continue.");
        EventInfoFrame frame = new EventInfoFrame(methodFailed("message", "", assertionError));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
