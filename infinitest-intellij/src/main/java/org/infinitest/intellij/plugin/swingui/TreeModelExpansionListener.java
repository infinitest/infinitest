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
			@Override
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
