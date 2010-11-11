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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import com.intellij.facet.Facet;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.ui.configuration.FacetsProvider;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;

public class FakeFacetEditorContext implements FacetEditorContext
{
    private final Module module;

    public FakeFacetEditorContext(Module module)
    {
        this.module = module;
    }

    public Project getProject()
    {
        return null;
    }

    public Library findLibrary(@NotNull String name)
    {
        return null;
    }

    public ModuleBuilder getModuleBuilder()
    {
        return null;
    }

    public boolean isNewFacet()
    {
        return false;
    }

    public Facet<?> getFacet()
    {
        return null;
    }

    public Module getModule()
    {
        return module;
    }

    public Facet<?> getParentFacet()
    {
        return null;
    }

    @NotNull
    public FacetsProvider getFacetsProvider()
    {
        return null;
    }

    @NotNull
    public ModulesProvider getModulesProvider()
    {
        return null;
    }

    public ModifiableRootModel getModifiableRootModel()
    {
        return null;
    }

    public ModuleRootModel getRootModel()
    {
        return null;
    }

    public Library[] getLibraries()
    {
        return new Library[0];
    }

    public WizardContext getWizardContext()
    {
        return null;
    }

    public Library createProjectLibrary(@NonNls String name, VirtualFile[] roots, VirtualFile[] sources)
    {
        return null;
    }

    public VirtualFile[] getLibraryFiles(Library library, OrderRootType rootType)
    {
        return new VirtualFile[0];
    }

    @NotNull
    public String getFacetName()
    {
        return null;
    }

    public <T> T getUserData(Key<T> key)
    {
        return null;
    }

    public <T> void putUserData(Key<T> key, T value)
    {
        // nothing to do here
    }
}
