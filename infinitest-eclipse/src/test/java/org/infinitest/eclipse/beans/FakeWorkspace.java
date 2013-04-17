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
package org.infinitest.eclipse.beans;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.mockito.*;

@SuppressWarnings("deprecation")
public class FakeWorkspace implements IWorkspace {
	public void addResourceChangeListener(IResourceChangeListener arg0) {
		throw new UnsupportedOperationException();
	}

	public void addResourceChangeListener(IResourceChangeListener arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	public ISavedState addSaveParticipant(Plugin arg0, ISaveParticipant arg1) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void build(int arg0, IProgressMonitor arg1) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void checkpoint(boolean arg0) {
		throw new UnsupportedOperationException();
	}

	public IProject[][] computePrerequisiteOrder(IProject[] arg0) {
		throw new UnsupportedOperationException();
	}

	public ProjectOrder computeProjectOrder(IProject[] arg0) {
		throw new UnsupportedOperationException();
	}

	public IStatus copy(IResource[] arg0, IPath arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IStatus copy(IResource[] arg0, IPath arg1, int arg2, IProgressMonitor arg3) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IStatus delete(IResource[] arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IStatus delete(IResource[] arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void deleteMarkers(IMarker[] arg0) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void forgetSavedTree(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Map<?, ?> getDanglingReferences() {
		throw new UnsupportedOperationException();
	}

	public IWorkspaceDescription getDescription() {
		throw new UnsupportedOperationException();
	}

	public IProjectNatureDescriptor getNatureDescriptor(String arg0) {
		throw new UnsupportedOperationException();
	}

	public IProjectNatureDescriptor[] getNatureDescriptors() {
		throw new UnsupportedOperationException();
	}

	public IPathVariableManager getPathVariableManager() {
		throw new UnsupportedOperationException();
	}

	public IWorkspaceRoot getRoot() {
		return Mockito.mock(IWorkspaceRoot.class);
	}

	public IResourceRuleFactory getRuleFactory() {
		throw new UnsupportedOperationException();
	}

	public ISynchronizer getSynchronizer() {
		throw new UnsupportedOperationException();
	}

	public boolean isAutoBuilding() {
		throw new UnsupportedOperationException();
	}

	public boolean isTreeLocked() {
		throw new UnsupportedOperationException();
	}

	public IProjectDescription loadProjectDescription(IPath arg0) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IProjectDescription loadProjectDescription(InputStream arg0) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IStatus move(IResource[] arg0, IPath arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IStatus move(IResource[] arg0, IPath arg1, int arg2, IProgressMonitor arg3) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IProjectDescription newProjectDescription(String arg0) {
		throw new UnsupportedOperationException();
	}

	public void removeResourceChangeListener(IResourceChangeListener arg0) {
		throw new UnsupportedOperationException();
	}

	public void removeSaveParticipant(Plugin arg0) {
		throw new UnsupportedOperationException();
	}

	public void run(IWorkspaceRunnable arg0, IProgressMonitor arg1) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void run(IWorkspaceRunnable arg0, ISchedulingRule arg1, int arg2, IProgressMonitor arg3) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public IStatus save(boolean arg0, IProgressMonitor arg1) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setDescription(IWorkspaceDescription arg0) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void setWorkspaceLock(WorkspaceLock arg0) {
		throw new UnsupportedOperationException();
	}

	public String[] sortNatureSet(String[] arg0) {
		throw new UnsupportedOperationException();
	}

	public IStatus validateEdit(IFile[] arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	public IStatus validateLinkLocation(IResource arg0, IPath arg1) {
		throw new UnsupportedOperationException();
	}

	public IStatus validateLinkLocationURI(IResource arg0, URI arg1) {
		throw new UnsupportedOperationException();
	}

	public IStatus validateName(String arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	public IStatus validateNatureSet(String[] arg0) {
		throw new UnsupportedOperationException();
	}

	public IStatus validatePath(String arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	public IStatus validateProjectLocation(IProject arg0, IPath arg1) {
		throw new UnsupportedOperationException();
	}

	public IStatus validateProjectLocationURI(IProject arg0, URI arg1) {
		throw new UnsupportedOperationException();
	}

	public Object getAdapter(Class arg0) {
		throw new UnsupportedOperationException();
	}
}
