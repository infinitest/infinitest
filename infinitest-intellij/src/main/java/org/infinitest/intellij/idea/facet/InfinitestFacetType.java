package org.infinitest.intellij.idea.facet;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.util.IconLoader;

public class InfinitestFacetType extends FacetType<InfinitestFacet, InfinitestFacetConfiguration>
{
    public static final InfinitestFacetType INSTANCE = new InfinitestFacetType();

    private InfinitestFacetType()
    {
        super(InfinitestFacet.ID, "Infinitest", "Infinitest");
    }

    @Override
    public InfinitestFacetConfiguration createDefaultConfiguration()
    {
        return new InfinitestFacetConfiguration();
    }

    @Override
    public InfinitestFacet createFacet(@NotNull Module module, String name,
                    @NotNull InfinitestFacetConfiguration configuration, @Nullable Facet underlyingFacet)
    {
        configuration.setModule(module);
        return new InfinitestFacet(this, module, name, configuration, underlyingFacet);
    }

    @Override
    public Icon getIcon()
    {
        return IconLoader.getIcon("/infinitest.png");
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType)
    {
        return StdModuleTypes.JAVA.equals(moduleType) || "PLUGIN_MODULE".equals(moduleType.getId());
    }
}
