package org.infinitest.eclipse;

import static org.infinitest.eclipse.prefs.PreferencesConstants.*;

import org.eclipse.ui.IStartup;

/**
 * Hook to launch the Infinitest plugin at start up if its preferences are configured to do so.
 */
public class ContinuousTestingStarter implements IStartup
{
    public void earlyStartup()
    {
        initializeAutoTesting();
    }

    private void initializeAutoTesting()
    {
        InfinitestPlugin plugin = InfinitestPlugin.getInstance();
        boolean autoTest = plugin.getPreferenceStore().getBoolean(AUTO_TEST);
        if (autoTest)
            plugin.startContinuouslyTesting();
    }
}
