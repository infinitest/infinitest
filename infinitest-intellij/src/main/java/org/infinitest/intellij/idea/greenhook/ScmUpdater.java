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
package org.infinitest.intellij.idea.greenhook;

import java.util.*;

import org.infinitest.intellij.idea.*;
import org.infinitest.intellij.plugin.greenhook.*;
import org.jetbrains.annotations.*;

import com.intellij.openapi.application.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.update.*;
import com.intellij.openapi.vfs.*;

public class ScmUpdater extends DefaultProjectComponent implements GreenHook {
	private final ProjectLevelVcsManager vcsManager;

	public ScmUpdater(Project project) {
		vcsManager = ProjectLevelVcsManager.getInstance(project);
	}

	@Override
	public void execute() {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			@Override
			public void run() {
				AbstractVcs[] vssProviders = vcsManager.getAllActiveVcss();
				for (AbstractVcs each : vssProviders) {
					UpdateEnvironment updateEnvironment = each.getUpdateEnvironment();
					if (updateEnvironment == null) {
						continue;
					}

					FilePath[] paths = collectVcsRoots(each);
					updateEnvironment.updateDirectories(paths, UpdatedFiles.create(), new EmptyProgressIndicator(), Ref.create((SequentialUpdatesContext) new InfinitestSequentialUpdatesContext()));
				}
				VirtualFileManager.getInstance().refresh(true);
			}
		});
	}

	private FilePath[] collectVcsRoots(AbstractVcs vcs) {
		List<FilePath> paths = new ArrayList<FilePath>();
		for (VirtualFile root : vcsManager.getRootsUnderVcs(vcs)) {
			paths.add(new FilePathImpl(root));
		}

		return paths.toArray(new FilePath[paths.size()]);
	}

	static class InfinitestSequentialUpdatesContext implements SequentialUpdatesContext {
		@Override
		@NotNull
		public String getMessageWhenInterruptedBeforeStart() {
			return "Infinitest generated message";
		}

		@Override
		public boolean shouldFail() {
			return false;
		}
	}
}
