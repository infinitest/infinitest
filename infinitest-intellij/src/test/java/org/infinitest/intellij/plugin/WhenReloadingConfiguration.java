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

import static org.mockito.Mockito.*;

import org.infinitest.intellij.plugin.launcher.*;
import org.junit.*;

public class WhenReloadingConfiguration {
	@Test
	public void shouldReplaceInfinitestLauncher() {
		InfinitestLauncher firstLauncher = mock(InfinitestLauncher.class);
		InfinitestLauncher secondLauncher = mock(InfinitestLauncher.class);

		FakeInfinitestConfiguration configuration = new FakeInfinitestConfiguration(firstLauncher, secondLauncher);
		new InfinitestPluginImpl(configuration);

		configuration.update();

		verify(firstLauncher).stop();
		verify(secondLauncher).launchInfinitest();
	}

	static class FakeInfinitestConfiguration implements InfinitestConfiguration {
		private InfinitestConfigurationListener listener;
		private final InfinitestLauncher firstLauncher;
		private final InfinitestLauncher secondLauncher;
		private boolean launched;

		FakeInfinitestConfiguration(InfinitestLauncher firstLauncher, InfinitestLauncher secondLauncher) {
			this.firstLauncher = firstLauncher;
			this.secondLauncher = secondLauncher;
		}

		public InfinitestLauncher createLauncher() {
			if (!launched) {
				launched = true;
				return firstLauncher;
			}
			return secondLauncher;
		}

		public void registerListener(InfinitestConfigurationListener listener) {
			this.listener = listener;
		}

		public void update() {
			listener.configurationUpdated(this);
		}
	}
}
