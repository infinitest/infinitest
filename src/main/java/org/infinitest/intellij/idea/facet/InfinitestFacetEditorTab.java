package org.infinitest.intellij.idea.facet;

import javax.swing.JComponent;

import org.infinitest.intellij.plugin.swingui.ConfigurationPane;
import org.jetbrains.annotations.Nls;

import com.intellij.facet.ui.FacetEditorTab;

public class InfinitestFacetEditorTab extends FacetEditorTab
{
    private final InfinitestFacetConfiguration configuration;
    private final ConfigurationPane configurationPane = new ConfigurationPane();

    public InfinitestFacetEditorTab(InfinitestFacetConfiguration configuration)
    {
        this.configuration = configuration;
        configurationPane.setScmUpdateEnabled(configuration.isScmUpdateEnabled());
    }

    @Nls
    public String getDisplayName()
    {
        return "Infinitest";
    }

    public JComponent createComponent()
    {
        return configurationPane;
    }

    public boolean isModified()
    {
        return configuration.isScmUpdateEnabled() != configurationPane.isScmUpdateEnabled();
    }

    public void apply()
    {
        configuration.setScmUpdateEnabled(configurationPane.isScmUpdateEnabled());
        configuration.updated();
    }

    public void reset()
    {
        configurationPane.setScmUpdateEnabled(configuration.isScmUpdateEnabled());
    }

    public void disposeUIResources()
    {
        // nothing to do here
    }

    public void setScmUpdateEnabled(boolean scmUpdateEnabled)
    {
        configurationPane.setScmUpdateEnabled(scmUpdateEnabled);
    }
}
