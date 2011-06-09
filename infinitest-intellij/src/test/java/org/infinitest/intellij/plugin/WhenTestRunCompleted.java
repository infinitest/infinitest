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
package org.infinitest.intellij.plugin;

import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.TestControl;
import org.infinitest.intellij.FakeInfinitestAnnotator;
import org.infinitest.intellij.plugin.launcher.InfinitestPresenter;
import org.infinitest.intellij.plugin.launcher.PresenterListener;
import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class WhenTestRunCompleted
{

    private InfinitestView mockView;
    private InfinitestPresenter presenter;
    private InfinitestCore mockCore;
    private PresenterListener mockPresenterListener;
    private PresenterListener mockPresenterListenerBis;

    @Before
    public void inContext()
    {
        mockView = mock(InfinitestView.class);
        mockCore = mock(InfinitestCore.class);
        mockPresenterListener = mock(PresenterListener.class);
        mockPresenterListenerBis = mock(PresenterListener.class);

        TestControl mockTestControl = mock(TestControl.class);

        presenter = new InfinitestPresenter(new ResultCollector(mockCore), mockCore, mockView, mockTestControl,
                        new FakeInfinitestAnnotator());
    }

    @Test
    public void shouldNotCallPresenterListener()
    {
        presenter.testRunComplete();

        verify(mockPresenterListener, never()).testRunCompleted();
    }

    @Test
    public void shouldDoNothingWhenNull()
    {
        presenter.addPresenterListener(null);

        presenter.testRunComplete();

        verify(mockPresenterListener, never()).testRunCompleted();
    }

    @Test
    public void shouldCallPresenterListener()
    {
        presenter.addPresenterListener(mockPresenterListener);

        presenter.testRunComplete();

        verify(mockPresenterListener).testRunCompleted();
    }

    @Test
    public void shouldCallTwoPresenterListener()
    {
        presenter.addPresenterListener(mockPresenterListener);
        presenter.addPresenterListener(mockPresenterListenerBis);

        presenter.testRunComplete();

        verify(mockPresenterListener).testRunCompleted();
        verify(mockPresenterListenerBis).testRunCompleted();
    }
}
