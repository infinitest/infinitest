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
package org.infinitest.intellij.idea.facet;

import javax.swing.*;

import org.infinitest.intellij.plugin.swingui.*;
import org.jetbrains.annotations.*;

import com.intellij.facet.ui.*;

public class InfinitestFacetEditorTab extends FacetEditorTab {
	private final InfinitestFacetConfiguration configuration;
	private final ConfigurationPane configurationPane = new ConfigurationPane();

	public InfinitestFacetEditorTab(InfinitestFacetConfiguration configuration) {
		this.configuration = configuration;
		configurationPane.setScmUpdateEnabled(configuration.isScmUpdateEnabled());
	}

	@Nls
	public String getDisplayName() {
		return "Infinitest";
	}

	public JComponent createComponent() {
		return configurationPane;
	}

	public boolean isModified() {
		return configuration.isScmUpdateEnabled() != configurationPane.isScmUpdateEnabled();
	}

	public void apply() {
		configuration.setScmUpdateEnabled(configurationPane.isScmUpdateEnabled());
		configuration.updated();
	}

	public void reset() {
		configurationPane.setScmUpdateEnabled(configuration.isScmUpdateEnabled());
	}

	public void disposeUIResources() {
		// nothing to do here
	}

	public void setScmUpdateEnabled(boolean scmUpdateEnabled) {
		configurationPane.setScmUpdateEnabled(scmUpdateEnabled);
	}
}
