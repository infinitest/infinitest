package org.infinitest.keys;

import static org.infinitest.util.Icon.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class WhenTrialLicensesAreMoreThan30DaysOld
{
    private TrialLicense license;

    @Before
    public void inContext()
    {
        license = new TrialLicense(new Date(0));
    }

    @Test
    public void shouldNoLongerBeValid()
    {
        assertFalse(license.isValid());
    }

    @Test
    public void shouldUseInvalidIcon()
    {
        assertEquals(INVALID, license.getIcon());
    }

    @Test
    public void shouldReportTrialPeriodElapsed()
    {
        assertEquals("Trial period elapsed", license.getSupportStatus());
    }
}
