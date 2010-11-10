package org.infinitest.intellij.idea.facet;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.components.ApplicationComponent;

public class InfinitestFacetLoader implements ApplicationComponent
{
    @NonNls
    @NotNull
    public String getComponentName()
    {
        return "InfinitestFacet";
    }

    public void initComponent()
    {
        FacetTypeRegistry.getInstance().registerFacetType(InfinitestFacetType.INSTANCE);
    }

    public void disposeComponent()
    {
        // nothing to do here
    }
}
