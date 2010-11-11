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

import static com.intellij.openapi.editor.markup.HighlighterLayer.*;

import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

public class InfinitestLineMarkersPass extends TextEditorHighlightingPass implements PsiClassVisitorAction
{
    private static final Key<InnerClassFriendlyTestEvent> KEY = new Key<InnerClassFriendlyTestEvent>("Infinitest");

    private final Project project;
    private final Document document;

    protected InfinitestLineMarkersPass(Project project, Document document)
    {
        super(project, document);
        this.project = project;
        this.document = document;
    }

    @Override
    public void doCollectInformation(ProgressIndicator progressIndicator)
    {
    }

    @Override
    public void doApplyInformationToEditor()
    {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null)
        {
            return;
        }
        psiFile.acceptChildren(new PsiClassVisitor(this));
    }

    public void execute(PsiClass psiClass)
    {
        MarkupModel model = document.getMarkupModel(project);
        clearInfinitestMarkersFrom(model);

        for (InnerClassFriendlyTestEvent each : IdeaInfinitestAnnotator.getInstance().getTestEvents())
        {
            String name = psiClass.getQualifiedName();
            if (name != null && name.equals(each.getPointOfFailureClassName()))
            {
                int line = each.getPointOfFailureLineNumber() - 1;
                RangeHighlighter rangeHighlighter = model.addLineHighlighter(line, FIRST, null);
                rangeHighlighter.setGutterIconRenderer(new InfinitestGutterIconRenderer(each));
                rangeHighlighter.putUserData(KEY, each);
            }
        }
    }

    private void clearInfinitestMarkersFrom(MarkupModel model)
    {
        for (RangeHighlighter each : model.getAllHighlighters())
        {
            if (each.getUserData(KEY) != null)
            {
                model.removeHighlighter(each);
            }
        }
    }
}
