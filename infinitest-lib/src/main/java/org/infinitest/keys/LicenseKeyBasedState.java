package org.infinitest.keys;

import static org.infinitest.keys.LicenseUtils.*;
import static org.infinitest.util.Icon.*;

import java.util.Date;

import org.infinitest.util.Icon;

class LicenseKeyBasedState implements LicenseState
{
    private final License license;
    private final boolean isValid;
    private String supportStatus;

    public LicenseKeyBasedState(License license, Date pluginReleaseDate)
    {
        this.license = license;
        isValid = validate(pluginReleaseDate);
    }

    private boolean isValidWithPluginReleasedOn(Date pluginReleaseDate)
    {
        return pluginReleaseDate.after(license.getExpirationDate());
    }

    public boolean validate(Date pluginReleaseDate)
    {
        if (license.noLicenseData())
        {
            return valid("30 Day Trial");
        }

        if (!license.supportedLicense())
        {
            return valid("No support available for this license");
        }

        String expirationDate = formatDate(license.getExpirationDate());
        if (isValidWithPluginReleasedOn(pluginReleaseDate))
        {
            return invalid("Invalid plugin version. Free upgrades ended " + expirationDate);
        }
        if (license.isExpired())
        {
            return valid("Upgrades and support expired " + formatDate(license.getExpirationDate()));
        }
        return valid("Upgrades and support expire " + expirationDate);
    }

    public String getLicenseName()
    {
        return license.getName();
    }

    public boolean isValid()
    {
        return isValid;
    }

    public String getSupportStatus()
    {
        return supportStatus;
    }

    public Icon getIcon()
    {
        if (!isValid)
        {
            return INVALID;
        }
        return license.getIcon();
    }

    private boolean valid(String message)
    {
        this.supportStatus = message;
        return true;
    }

    private boolean invalid(String message)
    {
        this.supportStatus = message;
        return false;
    }
}