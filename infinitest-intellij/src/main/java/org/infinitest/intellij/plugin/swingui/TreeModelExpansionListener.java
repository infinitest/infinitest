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
package org.infinitest.intellij.plugin.swingui;

import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * @author <a href="mailto:benrady@gmail.com">Ben Rady</a>
 */
class TreeModelExpansionListener {
	public static void watchTree(final JTree tree) {
		tree.expandPath(tree.getPathForRow(0));
		Class<?>[] interfaces = new Class<?>[] { TreeModelListener.class };
		InvocationHandler handler = new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) {
				if ("equals".equals(method.getName())) {
					return TreeModelExpansionListener.class.equals(args[0]);
				}
				if ("hashCode".equals(method.getName())) {
					return TreeModelExpansionListener.class.hashCode();
				}

				expandTreeNodes(tree);
				return null;
			}
		};
		ClassLoader classLoader = tree.getClass().getClassLoader();
		TreeModelListener listener = (TreeModelListener) Proxy.newProxyInstance(classLoader, interfaces, handler);
		tree.getModel().addTreeModelListener(listener);
	}

	private static void expandTreeNodes(JTree tree) {
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}
}
