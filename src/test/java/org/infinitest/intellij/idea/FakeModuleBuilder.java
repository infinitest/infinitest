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
