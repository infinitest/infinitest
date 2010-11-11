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

import com.intellij.openapi.module.Module;

public class FakeModuleBuilder
{
    private String name;
    private FacetListener facetListener;
    private String moduleFilePath;

    public static FakeModuleBuilder createFakeModule()
    {
        return new FakeModuleBuilder();
    }

    public FakeModuleBuilder withName(String aName)
    {
        this.name = aName;
        return this;
    }

    public FakeModuleBuilder withFacetListener(FacetListener listener)
    {
        this.facetListener = listener;
        return this;
    }

    public FakeModuleBuilder withModuleFilePath(String path)
    {
        this.moduleFilePath = path + System.getProperty("file.separator") + "test.iml";
        return this;
    }

    public Module build()
    {
        FakeModule module = new FakeModule();
        module.setName(name);
        module.setFacetListener(facetListener);
        module.setModuleFilePath(moduleFilePath);
        return module;
    }
}
