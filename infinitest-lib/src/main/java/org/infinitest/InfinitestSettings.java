package org.infinitest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class InfinitestSettings
{
    public static final String IS_ENABLED = "infinitest.isEnabled";
    private final Properties properties;

    public InfinitestSettings(InputStream inStream) throws IOException
    {
        this();
        properties.load(inStream);
    }

    public InfinitestSettings()
    {
        this.properties = new Properties();
        setIsInfinitestEnabled(true);
    }

    public boolean isInfinitestEnabled()
    {
        return Boolean.parseBoolean(properties.getProperty(IS_ENABLED));
    }

    public void saveTo(OutputStream outputStream) throws IOException
    {
        properties.store(outputStream, "");
    }

    public void setIsInfinitestEnabled(boolean enabled)
    {
        properties.setProperty(IS_ENABLED, Boolean.toString(enabled));
    }

}
