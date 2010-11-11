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
package org.infinitest.intellij.idea.facet;

import org.infinitest.intellij.idea.window.InfinitestToolWindow;
import org.jetbrains.annotations.NotNull;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;

public class InfinitestFacet extends Facet<InfinitestFacetConfiguration>
{
    public static final FacetTypeId<InfinitestFacet> ID = new FacetTypeId<InfinitestFacet>();
    private final Module module;

    public InfinitestFacet(@NotNull FacetType<?, ?> facetType, @NotNull Module module, String name,
                    @NotNull InfinitestFacetConfiguration configuration, Facet<?> underlyingFacet)
    {
        super(facetType, module, name, configuration, underlyingFacet);
        this.module = module;
    }

    @Override
    public void initFacet()
    {
        getWindow().facetInitialized();
    }

    @Override
    public void disposeFacet()
    {
        super.disposeFacet();
        getWindow().facetDisposed();
    }

    private FacetListener getWindow()
    {
        return module.getComponent(InfinitestToolWindow.class);
    }
}
