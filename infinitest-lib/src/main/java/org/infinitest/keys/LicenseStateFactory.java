package org.infinitest.keys;

import static org.apache.commons.lang.StringUtils.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

public class LicenseStateFactory
{
    private Date pluginReleaseDate;

    private final Date installationDate;

    public LicenseStateFactory(Date installationDate)
    {
        this.installationDate = installationDate;
        this.pluginReleaseDate = new Date();
    }

    public void setPluginReleaseDate(Date pluginReleaseDate)
    {
        this.pluginReleaseDate = pluginReleaseDate;
    }

    public LicenseState createLicense(String licenseKey)
    {
        if (isBlank(licenseKey))
        {
            return new TrialLicense(installationDate);
        }
        License license = parseLicense(licenseKey);
        if (license != null)
        {
            return new LicenseKeyBasedState(license, pluginReleaseDate);
        }
        return null;
    }

    private License parseLicense(String key)
    {
        try
        {
            License license = new License();
            license.setKey(key);
            return license;
        }
        catch (NegativeArraySizeException e)
        {
            log(Level.WARNING, "License key is not valid. Please re-enter");
        }
        catch (LicenseKeyException e)
        {
            log(Level.WARNING, "License key is not valid. Please re-enter");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error validating license key. Please re-install Infinitest.");
        }
        return null;
    }

}
