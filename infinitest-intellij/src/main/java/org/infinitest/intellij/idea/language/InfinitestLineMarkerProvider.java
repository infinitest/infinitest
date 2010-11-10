package org.infinitest.intellij.idea.language;

import java.util.Collection;
import java.util.List;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.psi.PsiElement;

public class InfinitestLineMarkerProvider implements LineMarkerProvider
{
    public LineMarkerInfo<?> getLineMarkerInfo(PsiElement psiElement)
    {
        return null;
    }

    public void collectSlowLineMarkers(List<PsiElement> psiElements, Collection<LineMarkerInfo> lineMarkerInfos)
    {
    }
}
