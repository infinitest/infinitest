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

		presenter = new InfinitestPresenter(mockResultCollector, mockCore, mockView, mockTestControl, new FakeInfinitestAnnotator());
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

	/*
	 * @Test FIXME: mocking Succeed/Failed state don't work for me public void
	 * shouldCallSucceed() {
	 * presenter.addPresenterListener(mockPresenterListener);
	 * when(presenter.isSuccess()).thenReturn(true);
	 * 
	 * presenter.onComplete();
	 * 
	 * verify(mockPresenterListener, never()).testRunFailed();
	 * verify(mockPresenterListener).testRunSucceed(); }
	 * 
	 * @Test public void shouldCallFailed() {
	 * presenter.addPresenterListener(mockPresenterListener);
	 * when(presenter.isSuccess()).thenReturn(false);
	 * 
	 * presenter.onComplete();
	 * 
	 * verify(mockPresenterListener).testRunFailed();
	 * verify(mockPresenterListener, never()).testRunSucceed(); }
	 */

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
