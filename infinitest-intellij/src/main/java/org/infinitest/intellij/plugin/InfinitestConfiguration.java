package org.infinitest.intellij.plugin;

import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;

public interface InfinitestConfiguration
{
    InfinitestLauncher createLauncher();

    void registerListener(InfinitestConfigurationListener listener);
}
