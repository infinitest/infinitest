package org.infinitest.intellij.idea;

import static com.intellij.psi.search.GlobalSearchScope.*;

import org.infinitest.intellij.plugin.SourceNavigator;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;

public class IdeaSourceNavigator implements SourceNavigator
{
    private String className;
    private final Project project;

    public IdeaSourceNavigator(Project project)
    {
        this.project = project;
    }

    public SourceNavigator open(String className)
    {
        this.className = className;
        return this;
    }

    public void line(int line)
    {
        VirtualFile source = fileForClass();
        if (source != null)
        {
            FileEditorManager.getInstance(project).openEditor(new OpenFileDescriptor(project, source, line - 1, 0),
                            true);
        }
    }

    @Nullable
    private VirtualFile fileForClass()
    {
        PsiClass clazz = JavaPsiFacade.getInstance(project).findClass(className, projectScope(project));
        if (clazz != null && clazz.getContainingFile() != null)
        {
            return clazz.getContainingFile().getVirtualFile();
        }
        return null;
    }
}
