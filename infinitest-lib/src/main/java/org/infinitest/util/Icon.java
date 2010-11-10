package org.infinitest.util;

import java.io.InputStream;
import java.net.URL;

public enum Icon
{
    TRIAL, INDIVIDUAL, DEVELOPER, COMMERCIAL, WARNING, INVALID;

    public String filename()
    {
        return name().toLowerCase();
    }

    public URL getLargeFormatUrl()
    {
        return getClass().getResource(largeFormatIconPath());
    }

    private String largeFormatIconPath()
    {
        return "/icons/" + filename() + ".png";
    }

    public InputStream getLargeFormatStream()
    {
        return getClass().getResourceAsStream(largeFormatIconPath());
    }

    public InputStream getSmallFormatStream()
    {
        return getClass().getResourceAsStream("/icons/" + filename() + "16.png");
    }
}