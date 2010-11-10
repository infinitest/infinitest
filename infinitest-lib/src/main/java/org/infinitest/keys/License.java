package org.infinitest.keys;

import static java.lang.Long.*;
import static org.infinitest.keys.License.Field.*;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.infinitest.util.Icon;

public class License
{
    public static final String EXPIRED_BY_DEFAULT = "0";

    public enum Type
    {
        INDIVIDUAL("Individual Use License", Icon.INDIVIDUAL, false), // Non-Corporate use license
        COMMERCIAL("Corporate Use License", Icon.COMMERCIAL, true), // Corp use, fully transferable
        DEVELOPER("Single Developer License", Icon.DEVELOPER, true); // Corp use, non-transferable

        private final String licenseName;
        private final Icon icon;
        private final boolean supported;

        Type(String licenseName, Icon icon, boolean supported)
        {
            this.licenseName = licenseName;
            this.icon = icon;
            this.supported = supported;
        }
    }

    public enum Field
    {
        // NOTE: Changing these values will break existing licenses. They must be deprecated for one
        // year before you can remove or change them.
        EXPIRATION_DATE,
        CUSTOMER_NAME,
        CUSTOMER_EMAIL,
        COMPANY_NAME,
        PRODUCT_NAME,
        ORDER_METHOD,
        LICENSE_ID,
        LICENSE_TYPE
    }

    private Properties properties;

    public License()
    {
        properties = new Properties();
    }

    public void setKey(String licenseKey) throws IOException
    {
        properties.load(new LicenseKeyDecrypter().decrypt(licenseKey));
    }

    public Date getExpirationDate()
    {
        Field field = EXPIRATION_DATE;
        String timeStamp = getProperty(field, EXPIRED_BY_DEFAULT);
        return new Date(parseLong(timeStamp));
    }

    private String getProperty(Field key, String defaultValue)
    {
        return getProperties().getProperty(key.name(), defaultValue);
    }

    protected Properties getProperties()
    {
        return properties;
    }

    public boolean noLicenseData()
    {
        return !properties.containsKey(EXPIRATION_DATE.name());
    }

    public Icon getIcon()
    {
        if (isExpired() && supportedLicense())
            return Icon.WARNING;
        return getType().icon;
    }

    public boolean isExpired()
    {
        return getExpirationDate().before(new Date());
    }

    public boolean supportedLicense()
    {
        return getType().supported;
    }

    public Type getType()
    {
        return Type.valueOf(getProperty(LICENSE_TYPE, Type.COMMERCIAL.name()));
    }

    public String getName()
    {
        return getType().licenseName;
    }
}
