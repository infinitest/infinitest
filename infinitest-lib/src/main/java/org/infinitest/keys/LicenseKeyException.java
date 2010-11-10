package org.infinitest.keys;

public class LicenseKeyException extends RuntimeException
{
    private static final long serialVersionUID = -1L;

    public LicenseKeyException(Throwable e)
    {
        super(e);
    }

    public LicenseKeyException(String message)
    {
        super(message);
    }
}
