package org.infinitest.intellij;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.swing.JPanel;

import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;
import org.infinitest.intellij.plugin.launcher.InfinitestLauncherImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class WhenLaunchingInfinitest
{
    private ModuleSettings moduleSettings;

    @Before
    public void setUp()
    {
        moduleSettings = new FakeModuleSettings("foo");
    }

    @Test
    public void shouldNameToolWindowAfterModule()
    {
        ToolWindowRegistry registry = mock(ToolWindowRegistry.class);

        InfinitestLauncher launcher = new InfinitestLauncherImpl(moduleSettings, registry,
                        new FakeCompilationNotifier(), new FakeSourceNavigator());
        launcher.launchInfinitest();

        verify(registry).registerToolWindow(Matchers.any(JPanel.class), eq("Infinitest_foo"));
    }
}
