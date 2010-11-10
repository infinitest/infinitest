package org.infinitest.keys;

import static org.infinitest.util.Icon.*;

import java.util.Date;

import org.infinitest.util.Icon;

public class TrialLicense implements LicenseState
{
    private static final long MILLISECONDS_PER_DAY = 60 * 60 * 1000 * 24;
    private final Date installationDate;

    public TrialLicense(Date installationDate)
    {
        this.installationDate = installationDate;
    }

    public String getLicenseName()
    {
        return "30 Day Trial License";
    }

    public boolean isValid()
    {
        return !trialExpired();
    }

    public String getSupportStatus()
    {
        if (trialExpired())
            return "Trial period elapsed";
        return daysLeft() + " Days Remaining";
    }

    public Icon getIcon()
    {
        if (trialExpired())
            return INVALID;
        return TRIAL;
    }

    private int daysLeft()
    {
        long remainingTime = installationDate.getTime() - almost30DaysAgo();
        return 1 + (int) (remainingTime / MILLISECONDS_PER_DAY);
    }

    private boolean trialExpired()
    {
        return daysLeft() <= 0;
    }

    private long almost30DaysAgo()
    {
        long almost30Days = (30 * MILLISECONDS_PER_DAY) - 10;
        return System.currentTimeMillis() - almost30Days;
    }
}
