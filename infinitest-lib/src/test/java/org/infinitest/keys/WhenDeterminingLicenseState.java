package org.infinitest.keys;

import static org.hamcrest.Matchers.*;
import static org.infinitest.keys.SampleKeys.*;
import static org.infinitest.util.Icon.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class WhenDeterminingLicenseState
{
    private LicenseStateFactory factory;

    @Before
    public void inContext()
    {
        factory = new LicenseStateFactory(new Date());
    }

    @Test
    public void shouldUse30DayTrialForBlankLicense()
    {
        LicenseState state = factory.createLicense("");
        assertEquals(TRIAL, state.getIcon());
        assertTrue(state.isValid());
        assertThat(state, is(TrialLicense.class));
    }

    @Test
    public void shouldReturnInvalidLicenseIf30DayTrialIsExpired()
    {
        factory = new LicenseStateFactory(new Date(0));
        LicenseState state = factory.createLicense("");
        assertFalse(state.isValid());
        assertThat(state, is(TrialLicense.class));
    }

    @Test
    public void canCreateIndividualLicenseStates()
    {
        LicenseState state = factory.createLicense(INDIVIDUAL_LICENSE);
        assertEquals(INDIVIDUAL, state.getIcon());
        assertTrue(state.isValid());
        assertEquals("Individual Use License", state.getLicenseName());
        assertEquals("No support available for this license", state.getSupportStatus());
    }

    @Test
    public void canCreateDeveloperLicenseStates()
    {
        LicenseState state = factory.createLicense(DEVELOPER_LICENSE);
        assertEquals(DEVELOPER, state.getIcon());
        assertTrue(state.isValid());
        assertEquals("Single Developer License", state.getLicenseName());
        assertEquals("Upgrades and support expire 12/31/3000", state.getSupportStatus());
    }

    @Test
    public void shouldUseCommercialForValidLicense()
    {
        LicenseState state = factory.createLicense(VALID_KEY);
        assertEquals(COMMERCIAL, state.getIcon());
        assertTrue(state.isValid());
        assertEquals("Corporate Use License", state.getLicenseName());
        assertEquals("Upgrades and support expire 08/17/292278994", state.getSupportStatus());
    }

    @Test
    public void shouldUseWarningForExpiredLicense()
    {
        factory = new LicenseStateFactory(new Date());
        factory.setPluginReleaseDate(new Date(0));

        LicenseState state = factory.createLicense(EXPIRED_KEY);
        assertEquals(WARNING, state.getIcon());
        assertTrue(state.isValid());
        assertEquals("Corporate Use License", state.getLicenseName());
        assertEquals("Upgrades and support expired 12/31/1969", state.getSupportStatus());
    }

    @Test
    public void shouldUseErrorForInvalidLicense()
    {
        factory = new LicenseStateFactory(new Date(1));
        LicenseState state = factory.createLicense(EXPIRED_KEY);
        assertEquals(INVALID, state.getIcon());
        assertFalse(state.isValid());
        assertEquals("Corporate Use License", state.getLicenseName());
        assertEquals("Invalid plugin version. Free upgrades ended 12/31/1969", state.getSupportStatus());
    }
}
