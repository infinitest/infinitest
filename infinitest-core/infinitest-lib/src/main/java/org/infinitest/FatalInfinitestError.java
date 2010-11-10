package org.infinitest;

class FatalInfinitestError extends Error
{
    private static final long serialVersionUID = -1L;

    public FatalInfinitestError(String message, Throwable e)
    {
        super(message, e);
    }
}
