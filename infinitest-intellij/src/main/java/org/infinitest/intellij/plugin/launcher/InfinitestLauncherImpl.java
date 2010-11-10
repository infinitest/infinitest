package org.infinitest.intellij.plugin.launcher;

import org.apache.log4j.Logger;
import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.intellij.CompilationNotifier;
import org.infinitest.intellij.InfinitestLoggingListener;
import org.infinitest.intellij.ModuleSettings;
import org.infinitest.intellij.ToolWindowRegistry;
import org.infinitest.intellij.idea.IdeaCompilationListener;
import static org.infinitest.intellij.idea.window.InfinitestToolWindow.TOOL_WINDOW_ID;
import org.infinitest.intellij.plugin.SourceNavigator;
import org.infinitest.intellij.plugin.greenhook.GreenHook;
import org.infinitest.intellij.plugin.greenhook.GreenHookListener;
import org.infinitest.intellij.plugin.swingui.ResultClickListener;
import org.infinitest.intellij.plugin.swingui.SwingEventQueue;
import org.infinitest.util.InfinitestUtils;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public class InfinitestLauncherImpl implements InfinitestLauncher
{
    private ModuleSettings moduleSettings;
    private ToolWindowRegistry toolWindowRegistry;
    private CompilationNotifier compilationNotifier;
    private SourceNavigator navigator;
    private InfinitestBuilder infinitestBuilder;
    private IdeaCompilationListener testControl;
    private GreenHookListener greenHookListener;

    public InfinitestLauncherImpl(ModuleSettings moduleSettings, ToolWindowRegistry toolWindowRegistry,
                                  CompilationNotifier compilationNotifier,
                                  SourceNavigator navigator)
    {
        this.moduleSettings = moduleSettings;
        this.toolWindowRegistry = toolWindowRegistry;
        this.compilationNotifier = compilationNotifier;
        this.navigator = navigator;
        this.greenHookListener = new GreenHookListener();
        this.infinitestBuilder = createInfinitestBuilder();
    }

    public void launchInfinitest()
    {
        moduleSettings.writeToLogger(Logger.getLogger(getClass()));

        testControl = new IdeaCompilationListener(infinitestBuilder.getCore(), moduleSettings);
        initializeInfinitestLogging();
        registerInfinitestWindow();
        addCompilationStatusListener();
        addScmGreenHookListener();
        addResultClickListener();
    }

    private void addResultClickListener()
    {
        infinitestBuilder.addResultClickListener(new ResultClickListener(navigator));
    }

    public void addGreenHook(GreenHook hook)
    {
        greenHookListener.add(hook);
    }

    private void initializeInfinitestLogging()
    {
        InfinitestUtils.addLoggingListener(new InfinitestLoggingListener(infinitestBuilder.getView()));
    }

    private void addCompilationStatusListener()
    {
        compilationNotifier.addCompilationStatusListener(testControl);
    }

    private void addScmGreenHookListener()
    {
        infinitestBuilder.addStatusListener(greenHookListener);
    }

    private void registerInfinitestWindow()
    {
        JPanel rootPanel = new JPanel(new BorderLayout());

        rootPanel.add(infinitestBuilder.createPluginComponent(testControl), BorderLayout.CENTER);

        toolWindowRegistry.registerToolWindow(rootPanel, toolWindowId());
    }

    public void stop()
    {
        toolWindowRegistry.unregisterToolWindow(toolWindowId());
        infinitestBuilder.removeStatusListener(greenHookListener);
    }

    private String toolWindowId()
    {
        return TOOL_WINDOW_ID + "_" + moduleSettings.getName();
    }

    private InfinitestBuilder createInfinitestBuilder()
    {
        InfinitestCoreBuilder coreBuilder = new InfinitestCoreBuilder(moduleSettings.getRuntimeEnvironment(),
                new SwingEventQueue());
        return new InfinitestBuilder(coreBuilder.createCore());
    }
}
