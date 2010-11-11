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
package org.infinitest.intellij;

import static java.util.logging.Level.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.infinitest.util.LoggingListener;
import org.junit.Before;
import org.junit.Test;

public class WhenLoggingMessage
{
    private LoggingListener listener;
    private InfinitestView view;

    @Before
    public void setUp()
    {
        view = mock(InfinitestView.class);
        listener = new InfinitestLoggingListener(view);
    }

    @Test
    public void shouldDisplayMesageInView()
    {
        listener.logMessage(INFO, "test message");
        verify(view).writeLogMessage(contains("test message"));
    }

    @Test
    public void shouldIncludeLogLevelInDisplayedMessage()
    {
        listener.logMessage(INFO, "test message");
        verify(view).writeLogMessage(contains("INFO"));
    }

    @Test
    public void shouldIncludeOtherLogLevelInDisplayedMessage()
    {
        listener.logMessage(WARNING, "test message");
        verify(view).writeLogMessage(contains("WARNING"));
    }

    @Test
    public void shouldLeftAlignLogLevelInTenCharacterField()
    {
        listener.logMessage(SEVERE, "test message");
        verify(view).writeLogMessage(contains("SEVERE    "));
    }
}
