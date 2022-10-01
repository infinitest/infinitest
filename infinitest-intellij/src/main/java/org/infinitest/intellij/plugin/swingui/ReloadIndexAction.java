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
package org.infinitest.intellij.plugin.swingui;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.infinitest.InfinitestCore;
import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;

import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.module.Module;

public class ReloadIndexAction extends AbstractAction {
	private static final long serialVersionUID = -1L;

	private final Project project;

	public ReloadIndexAction(Project project) {
		this.project = project;
		
		Icon icon = new ImageIcon(packageRelativeResource("reload.png", this.getClass()));
		putValue(Action.SMALL_ICON, icon);
		putValue(Action.SHORT_DESCRIPTION, "Force rebuild of dependency graph");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (Module module : ModuleManager.getInstance(project).getModules()) {
			InfinitestLauncher launcher = module.getService(InfinitestLauncher.class);
			InfinitestCore core = launcher.getCore();
			core.reload();
		}
	}

	private static URL packageRelativeResource(String resourceName, Class<?> clazz) {
		String directoryPrefix = '/' + clazz.getPackage().getName().replace(".", "/") + '/';
		return clazz.getResource(directoryPrefix + resourceName);
	}
}
