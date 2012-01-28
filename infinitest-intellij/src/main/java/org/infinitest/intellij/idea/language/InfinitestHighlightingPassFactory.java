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
package org.infinitest.intellij.idea.language;

import org.jetbrains.annotations.*;

import com.intellij.codeHighlighting.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.module.*;
import com.intellij.psi.*;

public class InfinitestHighlightingPassFactory implements TextEditorHighlightingPassFactory {
	private final TextEditorHighlightingPassRegistrar passRegistrar;

	public InfinitestHighlightingPassFactory(TextEditorHighlightingPassRegistrar passRegistrar) {
		this.passRegistrar = passRegistrar;
	}

	public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile psiFile, @NotNull Editor editor) {
		Module module = ModuleUtil.findModuleForPsiElement(psiFile);
		if (module == null) {
			return null;
		}

		return new InfinitestLineMarkersPass(module.getProject(), editor.getDocument(), editor.getMarkupModel());
	}

	public void projectOpened() {
	}

	public void projectClosed() {
	}

	@NotNull
	public String getComponentName() {
		return "InfinitestHighlighPassFactory";
	}

	public void initComponent() {
		passRegistrar.registerTextEditorHighlightingPass(this, TextEditorHighlightingPassRegistrar.Anchor.LAST, Pass.UPDATE_ALL, true, true);
	}

	public void disposeComponent() {
	}
}
