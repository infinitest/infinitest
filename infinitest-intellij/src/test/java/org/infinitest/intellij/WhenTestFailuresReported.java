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

import static java.util.Collections.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.mockito.Mockito.*;

import java.util.*;

import junit.framework.*;

import org.infinitest.*;
import org.infinitest.intellij.plugin.launcher.*;
import org.infinitest.testrunner.*;
import org.junit.*;
import org.junit.Test;

public class WhenTestFailuresReported {
	private InfinitestAnnotator annotator;
	private InfinitestPresenter presenter;
	private final TestEvent failure = methodFailed("message", "test", "method", new AssertionFailedError());
	private static final List<TestEvent> EMPTY_LIST = emptyList();

	@Before
	public void setUp() {
		// InfinitestCore core = new FakeInfinitestCore();
		InfinitestCore core = mock(InfinitestCore.class);
		annotator = mock(InfinitestAnnotator.class);
		presenter = new InfinitestPresenter(new ResultCollector(core), core, new FakeInfinitestView(), new FakeTestControl(), annotator);
	}

	@Test
	public void shouldAnnotateFailuresAdded() {
		presenter.failureListChanged(singletonList(failure), EMPTY_LIST);

		verify(annotator).annotate(failure);
	}

	@Test
	public void shouldClearFailuresRemoved() {
		presenter.failureListChanged(EMPTY_LIST, singletonList(failure));

		verify(annotator).clearAnnotation(failure);
	}
}
