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
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;

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
		label.setIcon(loadIcon(value));
		return label;
	}

	private Icon loadIcon(Object node) {
		if (node instanceof String) {
			return new ImageIcon(getClass().getResource("/org/infinitest/intellij/plugin/swingui/error.png"));
		}
		
		if (node instanceof Module) {
			Module module = (Module) node;
			return moduleIconProvider.getIcon(module);
		}
		
		return new ImageIcon(getClass().getResource("/org/infinitest/intellij/plugin/swingui/failure.png"));
	}

}
