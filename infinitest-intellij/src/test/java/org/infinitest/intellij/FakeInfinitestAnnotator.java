package org.infinitest.intellij;

import org.infinitest.testrunner.TestEvent;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;

@SuppressWarnings("all")
public class FakeInfinitestAnnotator implements InfinitestAnnotator
{
    public void annotate(TestEvent event)
    {
    }

    public void clearAnnotation(TestEvent event)
    {
    }

    public void annotate(PsiElement psiElement, AnnotationHolder holder)
    {
    }
}
