/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest;

import static com.google.common.collect.Lists.newArrayList;
import static org.infinitest.CoreStatus.FAILING;
import static org.infinitest.CoreStatus.INDEXING;
import static org.infinitest.CoreStatus.PASSING;
import static org.infinitest.CoreStatus.SCANNING;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.FAILING_COLOR;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.PASSING_COLOR;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.UNKNOWN_COLOR;
import static org.infinitest.intellij.plugin.launcher.StatusMessages.getMessage;
import static org.infinitest.util.InfinitestUtils.formatTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.swing.Action;

import org.infinitest.intellij.IntellijMockBase;
import org.infinitest.intellij.plugin.launcher.InfinitestPresenter;
import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.junit.Before;
import org.junit.Test;

public class TestInfinitestPresenter extends IntellijMockBase {
	private InfinitestView mockView;
	private InfinitestPresenter presenter;
	private InfinitestCore mockCore;

	@Before
	public void inContext() {
		mockView = mock(InfinitestView.class);
		mockCore = mock(InfinitestCore.class);
		
		when(launcher.getCore()).thenReturn(mockCore);

		presenter = new InfinitestPresenter(project, mockView);
	}

	public void verifyMocks() {
		verify(mockView, times(2)).addAction(any(Action.class));
		verify(mockView).setAngerBasedOnTime(anyLong());
		verify(mockView).setStatusMessage(getMessage(SCANNING));
		verify(mockView).setCycleTime(formatTime(0L));
	}

	@Test
	public void shouldUpdateProgressWhenATestIsRun() {
		final int testsLeftToRun = 9;
		final int totalTests = 10;

		presenter.testQueueUpdated(new TestQueueEvent(tests(9), totalTests));

		verify(mockView).setProgress((1 + totalTests) - testsLeftToRun);
		verify(mockView).setMaximumProgress(totalTests);
		verify(mockView).setCurrentTest(tests(1).get(0));
		verifyMocks();
	}

	private List<String> tests(int count) {
		List<String> list = newArrayList();
		for (int i = 0; i < count; i++) {
			list.add("SomeTest " + i);
		}
		return list;
	}

	private void ensureStatusEventFired(CoreStatus oldStatus, CoreStatus newStatus) {
		presenter.coreStatusChanged(oldStatus, newStatus);
		verifyMocks();
	}

	@Test
	public void shouldChangeProgressBarToRedWhenChangedToFailedStatus() {
		when(mockView.getMaximumProgress()).thenReturn(100);

		ensureStatusEventFired(null, FAILING);

		verify(mockView).setProgressBarColor(FAILING_COLOR);
		verify(mockView).setProgress(100);
		verify(mockView).setStatusMessage(getMessage(FAILING));
		verify(mockView).setCurrentTest("");
	}

	@Test
	public void shouldChangeProgressBarToGreenWhenChangedToPassingStatus() {
		when(mockView.getMaximumProgress()).thenReturn(100);

		ensureStatusEventFired(null, PASSING);

		verify(mockView).setProgressBarColor(PASSING_COLOR);
		verify(mockView).setStatusMessage(getMessage(PASSING));
		verify(mockView).setProgress(100);
		verify(mockView).setCurrentTest("");
	}

	@Test
	public void shouldClearResultTreeOnReload() {
		when(mockView.getMaximumProgress()).thenReturn(100);

		presenter.coreStatusChanged(null, INDEXING);

		verify(mockView).setProgress(100);
		verify(mockView).setStatusMessage(getMessage(INDEXING));
		verify(mockView).setProgressBarColor(UNKNOWN_COLOR);
	}
}
