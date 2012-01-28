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
package org.infinitest.intellij.idea;

import org.infinitest.intellij.*;

import com.intellij.openapi.compiler.*;
import com.intellij.openapi.project.*;

public class IdeaCompilationNotifier implements CompilationNotifier {
	private final Project project;

	public IdeaCompilationNotifier(Project project) {
		this.project = project;
	}

	public void addCompilationStatusListener(CompilationStatusListener compilationListener) {
		CompilerManager.getInstance(project).addCompilationStatusListener(compilationListener);
	}

	public void removeCompilationStatusListener(CompilationStatusListener compilationListener) {
		CompilerManager.getInstance(project).removeCompilationStatusListener(compilationListener);
	}
}
