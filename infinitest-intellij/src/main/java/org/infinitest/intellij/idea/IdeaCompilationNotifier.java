package org.infinitest.intellij.idea;

import org.infinitest.intellij.CompilationNotifier;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;

public class IdeaCompilationNotifier implements CompilationNotifier
{
    private final Project project;

    public IdeaCompilationNotifier(Project project)
    {
        this.project = project;
    }

    public void addCompilationStatusListener(CompilationStatusListener compilationListener)
    {
        CompilerManager.getInstance(project).addCompilationStatusListener(compilationListener);
    }
}
