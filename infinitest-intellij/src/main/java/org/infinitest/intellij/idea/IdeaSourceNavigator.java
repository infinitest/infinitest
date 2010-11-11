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
