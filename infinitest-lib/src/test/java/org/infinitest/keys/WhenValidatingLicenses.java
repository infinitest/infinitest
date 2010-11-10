package org.infinitest.keys;

import static org.infinitest.keys.License.Field.*;
import static org.infinitest.keys.SampleKeys.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.Properties;

import org.infinitest.keys.License.Type;
import org.junit.Before;
import org.junit.Test;

public class WhenValidatingLicenses
{
    private License license;
    private Properties properties;

    @Before
    public void inContext()
    {
        properties = new Properties();
        license = new License()
        {
            @Override
            protected Properties getProperties()
            {
                return properties;
            }
        };
    }

    @Test
    public void canLoadLicenseFromString() throws Exception
    {
        license = new License();
        license.setKey(VALID_KEY);
        assertEquals("foo@bar.com", license.getProperties().getProperty(CUSTOMER_EMAIL.name()));
        assertEquals(new Date(Long.MAX_VALUE), license.getExpirationDate());
        assertFalse(license.noLicenseData());
    }

    @Test
    public void shouldBeCommercialByDefault()
    {
        assertEquals(Type.COMMERCIAL, license.getType());

        properties.setProperty(LICENSE_TYPE.name(), Type.INDIVIDUAL.name());
        assertEquals(Type.INDIVIDUAL, license.getType());
    }

    @Test
    public void shouldExpireOnTheEpochByDefault()
    {
        assertEquals(new Date(0), license.getExpirationDate());
    }
}
