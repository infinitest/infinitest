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
package org.infinitest.intellij.idea;

import java.util.logging.Level;

import org.infinitest.util.InfinitestGlobalSettings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.SettingsCategory;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;

/**
 * A lightweight/application-level service to save the log level
 */
@Service(com.intellij.openapi.components.Service.Level.PROJECT)
@State(category = SettingsCategory.PLUGINS, name = "org.infinitest.intellij.idea.IdeaLogService.level", storages =  {
		@Storage(value = StoragePathMacros.NON_ROAMABLE_FILE, roamingType = RoamingType.DISABLED)
})
public final class IdeaLogService implements PersistentStateComponent<LogServiceState>, LogService {
	private LogServiceState state = new LogServiceState();
	
	public void loadState(LogServiceState state) {
		this.state = state;
		
		InfinitestGlobalSettings.setLogLevel(getLogLevel());
	}
	
	@Override
	public LogServiceState getState() {
		return state;
	}

	@Override
	public Level getLogLevel() {
		return state.getLogLevel();
	}

	@Override
	public void changeLogLevel(Level level) {
		InfinitestGlobalSettings.setLogLevel(level);
		state.setLevel(level.intValue());
	}
}
