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
package org.infinitest.intellij.idea.language;

import static com.intellij.openapi.editor.markup.HighlighterLayer.*;

import org.infinitest.intellij.InfinitestAnnotator;

import com.intellij.codeHighlighting.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;

public class InfinitestLineMarkersPass extends TextEditorHighlightingPass implements PsiClassVisitorAction {
	private static final Key<InnerClassFriendlyTestEvent> KEY = new Key<InnerClassFriendlyTestEvent>("Infinitest");

	private final Project project;
	private final Document document;
	private final MarkupModel model;

	protected InfinitestLineMarkersPass(Project project, Document document, MarkupModel model) {
		super(project, document);
		this.project = project;
		this.document = document;
		this.model = model;
	}

	@Override
	public void doCollectInformation(ProgressIndicator progressIndicator) {
		// Nothing to do here
	}

	@Override
	public void doApplyInformationToEditor() {
		PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
		if (psiFile == null) {
			return;
		}
		psiFile.acceptChildren(new PsiClassVisitor(this));
	}

	@Override
	public void execute(PsiClass psiClass) {
		clearInfinitestMarkersFrom(model);
		InfinitestAnnotator annotator = project.getService(InfinitestAnnotator.class);
		
		for (InnerClassFriendlyTestEvent each : annotator.getTestEvents()) {
			String name = psiClass.getQualifiedName();
			if ((name != null) && name.equals(each.getPointOfFailureClassName())) {
				int line = each.getPointOfFailureLineNumber() - 1;
				RangeHighlighter rangeHighlighter = model.addLineHighlighter(line, FIRST, null);
				rangeHighlighter.setGutterIconRenderer(new InfinitestGutterIconRenderer(each));
				rangeHighlighter.putUserData(KEY, each);
			}
		}
	}

	private void clearInfinitestMarkersFrom(MarkupModel model) {
		for (RangeHighlighter each : model.getAllHighlighters()) {
			if (each.getUserData(KEY) != null) {
				model.removeHighlighter(each);
			}
		}
	}
}
