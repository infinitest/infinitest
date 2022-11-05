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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.infinitest.ResultCollector;
import org.infinitest.TestControl;
import org.infinitest.intellij.InfinitestIcons;
import org.infinitest.intellij.idea.ProjectTestControl;
import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestEvent.TestState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.ui.RowIcon;
import com.intellij.util.ui.EmptyIcon;

/**
 * @author <a href="mailto:benrady@gmail.com".Ben Rady</a>
 */
class FailureCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(FailureCellRenderer.class);
	
	private ModuleIconProvider moduleIconProvider;

	public FailureCellRenderer() {
		setToolTipText("Double-click test nodes to navigate to source");
		
		try {
			// Checking if ModuleType.EMPTY is available
			// com.intellij.openapi.module.EmptyModuleType is not available during unit tests
			ModuleType.EMPTY.getDescription();
			moduleIconProvider = new ModuleTypeIconProvider();
		} catch (ExceptionInInitializerError | NoClassDefFoundError e) {
			LOGGER.debug("Error initializing ModuleTypeIconProvider", e);
			moduleIconProvider = ModuleIconProvider.NULL_MODULE_ICON_PROVIDER;
		}
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean focused) {
		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focused);
		
		if (value instanceof Module) {
			// The module's toStrint() 5 returns "module: 'XYZ'" and we just want to show the module's name
			Module module = (Module) value;
			label.setText(module.getName());
		}
		
		label.setIcon(loadIcon(value));
		return label;
	}

	private Icon loadIcon(Object node) {
		if (node instanceof TestEvent) {
			TestEvent testEvent = (TestEvent) node;
			if (testEvent.getType() == TestState.METHOD_FAILURE) {
				return AllIcons.RunConfigurations.TestFailed;
			}
		}
		
		if (node instanceof Module) {
			Module module = (Module) node;
			TestControl testControl = module.getProject().getService(ProjectTestControl.class);
			
			Icon moduleTypeIcon = moduleIconProvider.getIcon(module);
			Icon moduleStateIcon;
			
			if (testControl.shouldRunTests(module)) {
				ResultCollector collector = module.getService(InfinitestLauncher.class).getResultCollector();
				moduleStateIcon = InfinitestIcons.getIcon(collector.getStatus());
			} else {
				moduleStateIcon = InfinitestIcons.WAITING;
			}
			
			return new RowIcon(moduleStateIcon, EmptyIcon.create(2, 16), moduleTypeIcon);
		}
		
		return AllIcons.General.Warning;
	}

}
