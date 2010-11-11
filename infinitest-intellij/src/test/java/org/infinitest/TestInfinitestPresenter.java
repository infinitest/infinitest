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
package org.infinitest;

import static com.google.common.collect.Lists.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.*;
import static org.infinitest.intellij.plugin.launcher.StatusMessages.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.swing.Action;

import org.infinitest.intellij.FakeInfinitestAnnotator;
import org.infinitest.intellij.plugin.launcher.InfinitestPresenter;
import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.infinitest.testrunner.TestResultsListener;
import org.junit.Before;
import org.junit.Test;

public class TestInfinitestPresenter
{
    private InfinitestView mockView;
    private InfinitestPresenter presenter;
    private InfinitestCore mockCore;

    @Before
    public void inContext()
    {
        mockView = mock(InfinitestView.class);
        mockCore = mock(InfinitestCore.class);

        TestControl mockTestControl = mock(TestControl.class);
        when(mockTestControl.shouldRunTests()).thenReturn(true);

        presenter = new InfinitestPresenter(new ResultCollector(mockCore), mockCore, mockView, mockTestControl,
                        new FakeInfinitestAnnotator());
    }

    public void verifyMocks()
    {
        verify(mockView, times(2)).addAction(any(Action.class));
        verify(mockView).setAngerBasedOnTime(anyLong());
        verify(mockView).setStatusMessage(getMessage(SCANNING));
        verify(mockView).setCycleTime(formatTime(0L));

        verify(mockCore).addTestResultsListener(any(TestResultsListener.class));
        verify(mockCore, times(2)).addTestQueueListener(any(TestQueueListener.class));
        verify(mockCore).addDisabledTestListener(any(DisabledTestListener.class));
    }

    @Test
    public void shouldUpdateProgressWhenATestIsRun()
    {
        final int testsLeftToRun = 9;
        final int totalTests = 10;

        presenter.testQueueUpdated(new TestQueueEvent(tests(9), totalTests));

        verify(mockView).setProgress(1 + totalTests - testsLeftToRun);
        verify(mockView).setMaximumProgress(totalTests);
        verify(mockView).setCurrentTest(tests(1).get(0));
        verifyMocks();
    }

    private List<String> tests(int count)
    {
        List<String> list = newArrayList();
        for (int i = 0; i < count; i++)
        {
            list.add("SomeTest " + i);
        }
        return list;
    }

    private void ensureStatusEventFired(CoreStatus oldStatus, CoreStatus newStatus)
    {
        presenter.coreStatusChanged(oldStatus, newStatus);
        verifyMocks();
    }

    @Test
    public void shouldChangeProgressBarToRedWhenChangedToFailedStatus()
    {
        when(mockView.getMaximumProgress()).thenReturn(100);

        ensureStatusEventFired(null, FAILING);

        verify(mockView).setProgressBarColor(FAILING_COLOR);
        verify(mockView).setProgress(100);
        verify(mockView).setStatusMessage(getMessage(FAILING));
        verify(mockView).setCurrentTest("");
    }

    @Test
    public void shouldChangeProgressBarToGreenWhenChangedToPassingStatus()
    {
        when(mockView.getMaximumProgress()).thenReturn(100);

        ensureStatusEventFired(null, PASSING);

        verify(mockView).setProgressBarColor(PASSING_COLOR);
        verify(mockView).setStatusMessage(getMessage(PASSING));
        verify(mockView).setProgress(100);
        verify(mockView).setCurrentTest("");
    }

    @Test
    public void shouldClearResultTreeOnReload()
    {
        when(mockView.getMaximumProgress()).thenReturn(100);

        presenter.coreStatusChanged(null, INDEXING);

        verify(mockView).setProgress(100);
        verify(mockView).setStatusMessage(getMessage(INDEXING));
        verify(mockView).setProgressBarColor(UNKNOWN_COLOR);
    }
}
