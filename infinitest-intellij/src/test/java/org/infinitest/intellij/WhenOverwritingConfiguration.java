package org.infinitest.intellij;

import static java.lang.Boolean.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.infinitest.intellij.idea.facet.InfinitestFacetConfiguration;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

import com.intellij.openapi.util.WriteExternalException;

public class WhenOverwritingConfiguration
{
    private InfinitestFacetConfiguration configuration;
    private Element element;

    @Before
    public void setUp()
    {
        configuration = new InfinitestFacetConfiguration();
        element = new Element("config");
    }

    @Test
    public void shouldStoreScmUpdateEnabled() throws WriteExternalException
    {
        element.setAttribute("scmUpdateGreenHook", FALSE.toString());
        configuration.setScmUpdateEnabled(true);
        configuration.writeExternal(element);

        assertThat(element.getAttribute("scmUpdateGreenHook").getValue(), is(TRUE.toString()));
    }

    @Test
    public void shouldStoreScmUpdateDisabled() throws WriteExternalException
    {
        element.setAttribute("scmUpdateGreenHook", TRUE.toString());
        configuration.setScmUpdateEnabled(false);
        configuration.writeExternal(element);

        assertThat(element.getAttribute("scmUpdateGreenHook").getValue(), is(FALSE.toString()));
    }
}
