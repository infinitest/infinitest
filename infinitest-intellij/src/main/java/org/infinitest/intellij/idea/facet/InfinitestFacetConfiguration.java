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

import org.infinitest.intellij.idea.*;
import org.infinitest.intellij.idea.greenhook.*;
import org.infinitest.intellij.idea.window.*;
import org.infinitest.intellij.plugin.*;
import org.infinitest.intellij.plugin.launcher.*;
import org.jdom.*;

import com.intellij.facet.*;
import com.intellij.facet.ui.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.wm.*;

public class InfinitestFacetConfiguration implements FacetConfiguration, InfinitestConfiguration {
	private static final String SCM_UPDATE_GREEN_HOOK = "scmUpdateGreenHook";

	private boolean scmUpdateGreenHook;
	private String licenseKey;

	private Module module;
	private InfinitestConfigurationListener listener;

	public InfinitestFacetConfiguration() {
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
		return new FacetEditorTab[] { new InfinitestFacetEditorTab(this) };
	}

	public void readExternal(Element element) throws InvalidDataException {
		try {
			Attribute scmUpdateAttribute = element.getAttribute(SCM_UPDATE_GREEN_HOOK);
			if (scmUpdateAttribute != null) {
				scmUpdateGreenHook = scmUpdateAttribute.getBooleanValue();
			}

			Element licenseElement = element.getChild("license");
			if (licenseElement != null) {
				licenseKey = licenseElement.getValue();
			}
		} catch (DataConversionException e) {
			throw new InvalidDataException(e);
		}
	}

	public void writeExternal(Element configElement) throws WriteExternalException {
		configElement.setAttribute(SCM_UPDATE_GREEN_HOOK, Boolean.toString(scmUpdateGreenHook));

		Element licenseElement = configElement.getChild("license");
		if (licenseElement == null) {
			licenseElement = new Element("license");
			configElement.addContent(licenseElement);
		}
		licenseElement.setText(licenseKey);
	}

	public boolean isScmUpdateEnabled() {
		return scmUpdateGreenHook;
	}

	public void setScmUpdateEnabled(boolean scmUpdateGreenHook) {
		this.scmUpdateGreenHook = scmUpdateGreenHook;
	}

	public InfinitestLauncher createLauncher() {
		InfinitestLauncherImpl launcher = new InfinitestLauncherImpl(new IdeaModuleSettings(module), new IdeaToolWindowRegistry(module.getProject()), new IdeaCompilationNotifier(module.getProject()), new IdeaSourceNavigator(module.getProject()), FileEditorManager.getInstance(module.getProject()), ToolWindowManager.getInstance(module.getProject()));

		if (isScmUpdateEnabled()) {
			launcher.addGreenHook(new ScmUpdater(module.getProject()));
		}

		return launcher;
	}

	public void registerListener(InfinitestConfigurationListener listener) {
		this.listener = listener;
	}

	public void updated() {
		if (listener != null) {
			listener.configurationUpdated(this);
		}
	}
}
