package org.infinitest;

public interface StatusChangeListener
{
    void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus);
}
