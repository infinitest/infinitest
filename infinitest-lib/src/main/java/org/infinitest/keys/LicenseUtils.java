package org.infinitest.keys;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;

public abstract class LicenseUtils
{
    private static final String KEY_FOOTER = "</infinitest-key>";
    private static final String KEY_HEADER = "<infinitest-key>";

    public static byte[] getKeyBytes(String keyName) throws IOException
    {
        String keyLocation = "/org/infinitest/keys/" + keyName;
        InputStream keyStream = LicenseUtils.class.getResourceAsStream(keyLocation);
        if (keyStream == null)
            throw new IllegalStateException("Could not find key at " + keyLocation);
        return IOUtils.toByteArray(keyStream);
    }

    public static String stripWrapper(String licenseKey)
    {
        return licenseKey.replace(KEY_HEADER, "").replace(KEY_FOOTER, "").trim();
    }

    public static String wrapKey(String rawKey)
    {
        return KEY_HEADER + "\n" + rawKey + KEY_FOOTER + "\n";
    }

    public static String formatDate(Date date)
    {
        return dateFormat().format(date);
    }

    public static Date parseDate(String datestampString) throws ParseException
    {
        return dateFormat().parse(datestampString);
    }

    private static SimpleDateFormat dateFormat()
    {
        return new SimpleDateFormat("MM/dd/yyyy");
    }

}
