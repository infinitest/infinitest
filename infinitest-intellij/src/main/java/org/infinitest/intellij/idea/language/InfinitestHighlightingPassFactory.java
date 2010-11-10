package org.infinitest.intellij.idea.language;

import org.jetbrains.annotations.NotNull;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiFile;

public class InfinitestHighlightingPassFactory implements TextEditorHighlightingPassFactory
{
    private TextEditorHighlightingPassRegistrar passRegistrar;

    public InfinitestHighlightingPassFactory(TextEditorHighlightingPassRegistrar passRegistrar)
    {
        this.passRegistrar = passRegistrar;
    }

    public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile psiFile, @NotNull Editor editor)
    {
        Module module = ModuleUtil.findModuleForPsiElement(psiFile);
        if (module == null)
        {
            return null;
        }
        return new InfinitestLineMarkersPass(module.getProject(), editor.getDocument());
    }

    public void projectOpened()
    {
    }

    public void projectClosed()
    {
    }

    @NotNull
    public String getComponentName()
    {
        return "InfinitestHighlighPassFactory";
    }

    public void initComponent()
    {
        passRegistrar.registerTextEditorHighlightingPass(this, TextEditorHighlightingPassRegistrar.Anchor.LAST,
                Pass.UPDATE_ALL, true, true);
    }

    public void disposeComponent()
    {
    }
}
