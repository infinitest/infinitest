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

import static java.awt.Color.*;
import static org.mockito.Mockito.*;

import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.intellij.FakeInfinitestAnnotator;
import org.infinitest.intellij.FakeTestControl;
import org.infinitest.intellij.plugin.launcher.InfinitestPresenter;
import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.junit.Test;

public class WhenInitializingProgressBar
{
    @Test
    public void shouldSetBackgroundColorToBlack()
    {
        InfinitestView view = mock(InfinitestView.class);
        createPresenterWith(view);
        verify(view).setProgressBarColor(BLACK);
    }

    @Test
    public void shouldSetMaximumProgress()
    {
        InfinitestView view = mock(InfinitestView.class);
        createPresenterWith(view);
        verify(view).setMaximumProgress(1);
        verify(view).setProgress(1);
    }

    private void createPresenterWith(InfinitestView view)
    {
        new InfinitestPresenter(new ResultCollector(), mock(InfinitestCore.class), view, new FakeTestControl(),
                        new FakeInfinitestAnnotator());
    }
}
