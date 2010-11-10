package org.infinitest;

public class QueueDispatchException extends RuntimeException
{
    private static final long serialVersionUID = 9201658187855925789L;

    public QueueDispatchException(Throwable e)
    {
        super(e);
    }
}
