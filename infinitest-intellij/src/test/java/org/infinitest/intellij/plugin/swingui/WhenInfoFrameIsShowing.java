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

import static com.google.common.collect.Lists.*;
import static org.infinitest.intellij.plugin.swingui.EventInfoFrame.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.swing.JFrame;

import org.infinitest.testrunner.TestEvent;
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

    private static TestEvent withATest()
    {
        return new TestEvent(TEST_CASE_STARTING, "", "", "", null);
    }
}
