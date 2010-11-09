package org.infinitest.eclipse.status;

// DEBT Need a concrete WorkspaceStatus and a StatusUpdate interface with an apply() method that changes it

public interface WorkspaceStatus
{
    String getMessage();

    String getToolTip();

    boolean warningMessage();
}
