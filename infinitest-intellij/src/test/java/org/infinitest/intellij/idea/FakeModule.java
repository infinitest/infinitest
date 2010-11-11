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

import org.infinitest.intellij.idea.facet.FacetListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.PicoContainer;

import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.ComponentConfig;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.messages.MessageBus;

public class FakeModule implements Module
{
    private String name;
    private FacetListener listener;
    private String moduleFilePath;

    @Nullable
    public VirtualFile getModuleFile()
    {
        return null;
    }

    @NotNull
    public String getModuleFilePath()
    {
        return moduleFilePath;
    }

    @NotNull
    public ModuleType<?> getModuleType()
    {
        return null;
    }

    @NotNull
    public Project getProject()
    {
        return null;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    public BaseComponent getComponent(String aName)
    {
        return null;
    }

    @SuppressWarnings({ "unchecked" })
    public <T> T getComponent(Class<T> interfaceClass)
    {
        return (T) listener;
    }

    public <T> T getComponent(Class<T> interfaceClass, T defaultImplementationIfAbsent)
    {
        return null;
    }

    @NotNull
    public Class<?>[] getComponentInterfaces()
    {
        return new Class<?>[0];
    }

    public boolean hasComponent(@NotNull Class interfaceClass)
    {
        return false;
    }

    @NotNull
    public <T> T[] getComponents(Class<T> baseClass)
    {
        return null;
    }

    @NotNull
    public PicoContainer getPicoContainer()
    {
        return null;
    }

    public MessageBus getMessageBus()
    {
        return null;
    }

    public boolean isDisposed()
    {
        return false;
    }

    @NotNull
    public ComponentConfig[] getComponentConfigurations()
    {
        return new ComponentConfig[0];
    }

    @Nullable
    public Object getComponent(ComponentConfig componentConfig)
    {
        return null;
    }

    public <T> T[] getExtensions(ExtensionPointName<T> extensionPointName)
    {
        return null;
    }

    public ComponentConfig getConfig(Class componentImplementation)
    {
        return null;
    }

    public boolean isLoaded()
    {
        return false;
    }

    public boolean isSavePathsRelative()
    {
        return false;
    }

    public void setSavePathsRelative(boolean b)
    {
        // nothing to do here
    }

    public void setOption(@NotNull String optionName, @NotNull String optionValue)
    {
        // nothing to do here
    }

    public void clearOption(@NotNull String optionName)
    {
        // nothing to do here
    }

    @Nullable
    public String getOptionValue(@NotNull String optionName)
    {
        return null;
    }

    public GlobalSearchScope getModuleScope()
    {
        return null;
    }

    public GlobalSearchScope getModuleWithLibrariesScope()
    {
        return null;
    }

    public GlobalSearchScope getModuleWithDependenciesScope()
    {
        return null;
    }

    public GlobalSearchScope getModuleWithDependenciesAndLibrariesScope(boolean includeTests)
    {
        return null;
    }

    public GlobalSearchScope getModuleWithDependentsScope()
    {
        return null;
    }

    public GlobalSearchScope getModuleTestsWithDependentsScope()
    {
        return null;
    }

    public GlobalSearchScope getModuleRuntimeScope(boolean includeTests)
    {
        return null;
    }

    @Nullable
    public LanguageLevel getLanguageLevel()
    {
        return null;
    }

    @NotNull
    public LanguageLevel getEffectiveLanguageLevel()
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

    public void dispose()
    {
        // nothing to do here
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setFacetListener(FacetListener listener)
    {
        this.listener = listener;
    }

    public void setModuleFilePath(String moduleFilePath)
    {
        this.moduleFilePath = moduleFilePath;
    }
}
