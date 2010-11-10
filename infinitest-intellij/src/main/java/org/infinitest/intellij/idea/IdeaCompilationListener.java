package org.infinitest.intellij.idea;

import org.infinitest.InfinitestCore;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.TestControl;
import org.infinitest.intellij.ModuleSettings;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;

public class IdeaCompilationListener implements CompilationStatusListener, TestControl
{
    private final InfinitestCore core;
    private final ModuleSettings moduleSettings;
    private boolean shouldRunTests = true;

    public IdeaCompilationListener(InfinitestCore core, ModuleSettings moduleSettings)
    {
        this.core = core;
        this.moduleSettings = moduleSettings;
    }

    public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext)
    {
        RuntimeEnvironment runtimeEnvironment = moduleSettings.getRuntimeEnvironment();
        if (runtimeEnvironment == null)
        {
            return;
        }

        if (!aborted && errors == 0)
        {
            core.setRuntimeEnvironment(runtimeEnvironment);
            if (shouldRunTests)
            {
                core.update();
            }
        }
    }

    public void setRunTests(boolean shouldRunTests)
    {
        if (shouldRunTests && !this.shouldRunTests)
        {
            core.reload();
        }
        this.shouldRunTests = shouldRunTests;
    }

    public boolean shouldRunTests()
    {
        return shouldRunTests;
    }
}
