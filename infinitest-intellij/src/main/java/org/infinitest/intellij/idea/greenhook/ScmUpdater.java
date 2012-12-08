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

	public void execute() {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
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
		@NotNull
		public String getMessageWhenInterruptedBeforeStart() {
			return "Infinitest generated message";
		}

        public boolean shouldFail() {
            return false;
        }
    }
}
