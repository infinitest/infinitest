package org.infinitest.intellij;

import static java.lang.Boolean.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.keys.SampleKeys.*;
import static org.junit.Assert.*;

import org.infinitest.intellij.idea.facet.InfinitestFacetConfiguration;
import org.infinitest.keys.SampleKeys;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

import com.intellij.openapi.util.WriteExternalException;

public class WhenWritingConfigurationForTheFirstTime
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
        configuration.setScmUpdateEnabled(true);
        configuration.writeExternal(element);

        assertThat(element.getAttribute("scmUpdateGreenHook").getValue(), is(TRUE.toString()));
    }

    @Test
    public void shouldStoreScmUpdateDisabled() throws WriteExternalException
    {
        configuration.setScmUpdateEnabled(false);
        configuration.writeExternal(element);

        assertThat(element.getAttribute("scmUpdateGreenHook").getValue(), is(FALSE.toString()));
    }

    @Test
    public void shouldStoreTrialLicense() throws WriteExternalException
    {
        configuration.setLicenseKey(null);
        configuration.writeExternal(element);

        assertThat(element.getChild("license").getValue(), is(""));
    }

    @Test
    public void shouldStoreIndividualLicense() throws WriteExternalException
    {
        configuration.setLicenseKey(SampleKeys.INDIVIDUAL_LICENSE);
        configuration.writeExternal(element);

        assertThat(element.getChild("license").getValue(), is(SampleKeys.INDIVIDUAL_LICENSE));
    }

    @Test
    public void shouldStoreCorporateLicense() throws WriteExternalException
    {
        configuration.setLicenseKey(VALID_KEY);
        configuration.writeExternal(element);

        assertThat(element.getChild("license").getValue(), is(VALID_KEY));
    }
}
