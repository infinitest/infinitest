package org.infinitest.intellij.idea.language;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

public class PsiClassVisitor extends PsiElementVisitor
{
    private final PsiClassVisitorAction action;

    public PsiClassVisitor(PsiClassVisitorAction action)
    {
        this.action = action;
    }

    @Override
    public void visitElement(PsiElement psiElement)
    {
        if (psiElement instanceof PsiClass)
        {
            action.execute((PsiClass) psiElement);
        }
        super.visitElement(psiElement);
    }
}
