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

import static java.util.Arrays.*;
import static org.easymock.EasyMock.*;

import org.infinitest.TestQueueEvent;
import org.junit.Before;
import org.junit.Test;

public class ConsoleClearingListenerTest
{
    private TextOutputWriter writer;
    private ConsoleClearingListener listener;

    @Before
    public void inContext()
    {
        writer = createMock(TextOutputWriter.class);
        listener = new ConsoleClearingListener(writer);
    }

    @Test
    public void shouldClearConsoleWhenTestRunIsStarted()
    {
        writer.clearConsole();
        replay(writer);

        listener.testQueueUpdated(new TestQueueEvent(asList("test"), 1));
        verify(writer);
    }

    @Test
    public void shouldNotClearTheConsoleWhenTestsAreRunning()
    {
        replay(writer);

        listener.testQueueUpdated(new TestQueueEvent(asList("test"), 2));
        verify(writer);
    }

    @Test
    public void shouldClearTheConsoleWhenTheCoreIsReloaded()
    {
        writer.clearConsole();
        replay(writer);

        listener.reloading();
        verify(writer);
    }
}
