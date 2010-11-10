package org.infinitest.intellij.idea;

import javax.swing.JComponent;

import com.intellij.facet.ui.FacetEditorValidator;
import com.intellij.facet.ui.FacetValidatorsManager;

@SuppressWarnings("all")
public class FakeFacetValidatorsManager implements FacetValidatorsManager
{
    public void registerValidator(FacetEditorValidator validator, JComponent... componentsToWatch)
    {
    }

    public void validate()
    {
    }
}
