package org.infinitest.intellij.idea.language;

import com.intellij.psi.PsiClass;

public interface PsiClassVisitorAction
{
    void execute(PsiClass psiClass);
}
