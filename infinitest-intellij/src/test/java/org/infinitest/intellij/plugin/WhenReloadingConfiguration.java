package org.infinitest.intellij.plugin;

import static org.mockito.Mockito.*;

import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;
import org.junit.Test;

public class WhenReloadingConfiguration
{
    @Test
    public void shouldReplaceInfinitestLauncher()
    {
        InfinitestLauncher firstLauncher = mock(InfinitestLauncher.class);
        InfinitestLauncher secondLauncher = mock(InfinitestLauncher.class);

        FakeInfinitestConfiguration configuration = new FakeInfinitestConfiguration(firstLauncher, secondLauncher);
        new InfinitestPluginImpl(configuration);

        configuration.update();

        verify(firstLauncher).stop();
        verify(secondLauncher).launchInfinitest();
    }

    static class FakeInfinitestConfiguration implements InfinitestConfiguration
    {
        private InfinitestConfigurationListener listener;
        private final InfinitestLauncher firstLauncher;
        private final InfinitestLauncher secondLauncher;
        private boolean launched;

        FakeInfinitestConfiguration(InfinitestLauncher firstLauncher, InfinitestLauncher secondLauncher)
        {
            this.firstLauncher = firstLauncher;
            this.secondLauncher = secondLauncher;
        }

        public InfinitestLauncher createLauncher()
        {
            if (!launched)
            {
                launched = true;
                return firstLauncher;
            }
            return secondLauncher;
        }

        public void registerListener(InfinitestConfigurationListener listener)
        {
            this.listener = listener;
        }

        public void update()
        {
            listener.configurationUpdated(this);
        }
    }
}
