package org.infinitest.eclipse;

import org.infinitest.eclipse.trim.VisualStatus;

public interface PluginActivationController
{
    void enable();

    void disable();

    void attachVisualStatus(VisualStatus status);
}