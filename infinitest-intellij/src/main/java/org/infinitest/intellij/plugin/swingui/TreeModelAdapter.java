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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.infinitest.FailureListListener;
import org.infinitest.ResultCollector;
import org.infinitest.TestControl;
import org.infinitest.intellij.idea.ProjectTestControl;
import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;
import org.infinitest.testrunner.PointOfFailure;
import org.infinitest.testrunner.TestEvent;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.util.Function;

/**
 * The structure of the tree is:<br>
 * {@link Project} / {@link Module} / {@link PointOfFailure} / {@link TestEvent}
 */
public class TreeModelAdapter implements TreeModel, FailureListListener, ModuleListener {
	private final Project project;
	private final List<TreeModelListener> listeners;

	public TreeModelAdapter(Project project) {
		this.project = project;
		listeners = newArrayList();
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent.equals(getRoot())) {
			return ModuleManager.getInstance(project).getModules()[index];
		} else if (parent instanceof Module) {
			Module module = (Module) parent;
			ResultCollector collector = module.getService(InfinitestLauncher.class).getResultCollector();
			return collector.getPointOfFailure(index);
		} else {
			PointOfFailure pointOfFailure = (PointOfFailure) parent;
			
			for (Module module : ModuleManager.getInstance(project).getModules()) {
				ResultCollector collector = module.getService(InfinitestLauncher.class).getResultCollector();
				if (collector.isPointOfFailure(parent)) {
					return collector.getTestsFor(pointOfFailure).get(index);
				}
			}
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent.equals(getRoot())) {
			return ModuleManager.getInstance(project).getModules().length;
		} else if (parent instanceof Module) {
			Module module = (Module) parent;
			TestControl testControl = module.getProject().getService(ProjectTestControl.class);
			
			if (testControl.shouldRunTests(module)) {
				ResultCollector collector = module.getService(InfinitestLauncher.class).getResultCollector();
				return collector.getPointOfFailureCount();
			}
		} else if (parent instanceof PointOfFailure) {
			PointOfFailure pointOfFailure = (PointOfFailure) parent;
			
			for (Module module : ModuleManager.getInstance(project).getModules()) {
				ResultCollector collector = module.getService(InfinitestLauncher.class).getResultCollector();
				if (collector.isPointOfFailure(parent)) {
					return collector.getTestsFor(pointOfFailure).size();
				}
			}
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent.equals(getRoot())) {
			Module[] modules = ModuleManager.getInstance(project).getModules();
			for (int i=0; i<modules.length; i++) {
				if (child.equals(modules[i])) {
					return i;
				}
			}
		} else if (parent instanceof Module) {
			Module module = (Module) parent;
			ResultCollector collector = module.getService(InfinitestLauncher.class).getResultCollector();
			return collector.getPointOfFailureIndex((PointOfFailure) child);
		}
		return 0;
	}

	@Override
	public Object getRoot() {
		return project;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// User changes are ignored
	}

	protected void fireTreeStructureChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int[] childIndices = new int[0];
				Object[] children = new Object[0];
				for (TreeModelListener listener : listeners) {
					listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(getRoot()), childIndices, children));
				}
			}
		});
	}

	@Override
	public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved) {
		fireTreeStructureChanged();
	}

	@Override
	public void failuresUpdated(Collection<TestEvent> updatedFailures) {
		fireTreeStructureChanged();
	}
	
	@Override
	public void moduleAdded(@NotNull Project project, @NotNull Module module) {
		fireTreeStructureChanged();
	}
	
	@Override
	public void moduleRemoved(@NotNull Project project, @NotNull Module module) {
		fireTreeStructureChanged();
	}
	
	@Override
	public void modulesRenamed(@NotNull Project project, @NotNull List<? extends Module> modules, @NotNull Function<? super Module, String> oldNameProvider) {
		fireTreeStructureChanged();
	}
}
