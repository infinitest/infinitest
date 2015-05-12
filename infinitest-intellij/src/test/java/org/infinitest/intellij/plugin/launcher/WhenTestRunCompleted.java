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
package org.infinitest.intellij.plugin.launcher;

import static org.mockito.Mockito.*;

import org.infinitest.*;
import org.infinitest.intellij.*;
import org.infinitest.intellij.plugin.swingui.*;
import org.junit.*;

public class WhenTestRunCompleted {

	private InfinitestView mockView;
	private InfinitestPresenter presenter;
	private InfinitestCore mockCore;
	private PresenterListener mockPresenterListener;
	private PresenterListener mockPresenterListenerBis;
	private ResultCollector mockResultCollector;

	@Before
	public void inContext() {
		mockView = mock(InfinitestView.class);
		mockCore = mock(InfinitestCore.class);
		mockPresenterListener = mock(PresenterListener.class);
		mockPresenterListenerBis = mock(PresenterListener.class);
		mockResultCollector = new ResultCollector(mockCore);

		TestControl mockTestControl = mock(TestControl.class);

		presenter = spy(new InfinitestPresenter(mockResultCollector, mockCore, mockView, mockTestControl, new FakeInfinitestAnnotator()));
	}

	@Test
	public void shouldNotCallPresenterListener() {
		presenter.onComplete();

		verify(mockPresenterListener, never()).testRunCompleted();
		verify(mockPresenterListener, never()).testRunStarted();
		verify(mockPresenterListener, never()).testRunWaiting();
	}

	@Test
	public void shouldDoNothingWhenNull() {
		presenter.addPresenterListener(null);

		presenter.onComplete();

		verify(mockPresenterListener, never()).testRunCompleted();
	}

	@Test
	public void shouldCallCompleted() {
		presenter.addPresenterListener(mockPresenterListener);

		presenter.onComplete();

		verify(mockPresenterListener).testRunCompleted();
		verify(mockPresenterListener, never()).testRunStarted();
		verify(mockPresenterListener, never()).testRunWaiting();
	}

	@Test
	public void shouldCallCompletedTwice() {
		presenter.addPresenterListener(mockPresenterListener);
		presenter.addPresenterListener(mockPresenterListenerBis);

		presenter.onComplete();

		verify(mockPresenterListener).testRunCompleted();
		verify(mockPresenterListenerBis).testRunCompleted();
	}

    @Test
    public void shouldCallSucceed() {
        presenter.addPresenterListener(mockPresenterListener);
        when(presenter.isSuccess()).thenReturn(true);

        presenter.onComplete();

        verify(mockPresenterListener, never()).testRunFailed();
        verify(mockPresenterListener).testRunSucceed();
    }

    @Test
    public void shouldCallFailed() {
        presenter.addPresenterListener(mockPresenterListener);
        when(presenter.isSuccess()).thenReturn(false);

        presenter.onComplete();

        verify(mockPresenterListener).testRunFailed();
        verify(mockPresenterListener, never()).testRunSucceed();
    }

	@Test
	public void shouldCallWait() {
		presenter.addPresenterListener(mockPresenterListener);

		presenter.onWait();

		verify(mockPresenterListener, never()).testRunCompleted();
		verify(mockPresenterListener, never()).testRunStarted();
		verify(mockPresenterListener).testRunWaiting();
	}

	@Test
	public void shouldCallWaitTwice() {
		presenter.addPresenterListener(mockPresenterListener);
		presenter.addPresenterListener(mockPresenterListenerBis);

		presenter.onWait();

		verify(mockPresenterListener).testRunWaiting();
		verify(mockPresenterListenerBis).testRunWaiting();
	}

	@Test
	public void shouldCallRun() {
		presenter.addPresenterListener(mockPresenterListener);

		presenter.onRun();

		verify(mockPresenterListener, never()).testRunCompleted();
		verify(mockPresenterListener).testRunStarted();
		verify(mockPresenterListener, never()).testRunWaiting();
	}

	@Test
	public void shouldCallRunTwice() {
		presenter.addPresenterListener(mockPresenterListener);
		presenter.addPresenterListener(mockPresenterListenerBis);

		presenter.onRun();

		verify(mockPresenterListener).testRunStarted();
		verify(mockPresenterListenerBis).testRunStarted();
	}
}
