package org.infinitest.intellij.plugin;

import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;

public class InfinitestPluginImpl implements InfinitestPlugin, InfinitestConfigurationListener
{
    private InfinitestLauncher launcher;

    public InfinitestPluginImpl(InfinitestConfiguration configuration)
    {
        configuration.registerListener(this);
        launcher = configuration.createLauncher();
    }

    public void startInfinitest()
    {
        launcher.launchInfinitest();
    }

    public void stopInfinitest()
    {
        launcher.stop();
    }

    public void configurationUpdated(InfinitestConfiguration configuration)
    {
        stopInfinitest();
        launcher = configuration.createLauncher();
        startInfinitest();
    }
}
