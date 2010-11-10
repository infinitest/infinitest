package org.infinitest.keys;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class TrialLicenseTest
{
    private Calendar now;

    @Before
    public void inContext()
    {
        now = Calendar.getInstance();
    }

    @Test
    public void shouldReportRemainingDaysInStatus()
    {
        TrialLicense license = licenseCreated(daysAgo(10));
        assertEquals("20 Days Remaining", license.getSupportStatus());
    }

    @Test
    public void shouldReport30DaysForBrandNewLicense()
    {
        TrialLicense license = new TrialLicense(new Date());
        assertEquals("30 Days Remaining", license.getSupportStatus());
    }

    @Test
    public void shouldReport30DaysForMostlyNewLicense()
    {
        now.add(Calendar.HOUR, -1);
        Date anHourAgo = now.getTime();
        TrialLicense license = licenseCreated(anHourAgo);
        assertEquals("30 Days Remaining", license.getSupportStatus());
    }

    @Test
    public void shouldExpireWhenZeroDaysAreLeft()
    {
        TrialLicense license = licenseCreated(daysAgo(31));
        assertEquals("Trial period elapsed", license.getSupportStatus());
    }

    private Date daysAgo(int days)
    {
        now.add(Calendar.HOUR, -days * 24);
        Date tenDaysAgo = now.getTime();
        return tenDaysAgo;
    }

    private TrialLicense licenseCreated(Date daysAgo)
    {
        return new TrialLicense(daysAgo);
    }
}
