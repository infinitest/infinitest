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
