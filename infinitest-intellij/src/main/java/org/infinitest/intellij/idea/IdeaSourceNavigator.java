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
package org.infinitest.intellij.idea;

import static com.intellij.psi.search.GlobalSearchScope.*;

import org.infinitest.intellij.plugin.*;
import org.jetbrains.annotations.*;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;

public class IdeaSourceNavigator implements SourceNavigator {
	private String className;
	private final Project project;

	public IdeaSourceNavigator(Project project) {
		this.project = project;
	}

	@Override
	public SourceNavigator open(String className) {
		this.className = className;
		return this;
	}

	@Override
	public void line(int line) {
		VirtualFile source = fileForClass();
		if (source != null) {
			FileEditorManager.getInstance(project).openEditor(new OpenFileDescriptor(project, source, line - 1, 0), true);
		}
	}

	@Nullable
	private VirtualFile fileForClass() {
		PsiClass clazz = JavaPsiFacade.getInstance(project).findClass(className, allScope(project));
		if ((clazz != null) && (clazz.getContainingFile() != null)) {
			return clazz.getContainingFile().getVirtualFile();
		}
		return null;
	}
}
