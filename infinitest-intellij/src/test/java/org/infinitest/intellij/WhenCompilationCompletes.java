package org.infinitest.intellij;

import static org.mockito.Mockito.*;

import org.infinitest.InfinitestCore;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.intellij.idea.IdeaCompilationListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.intellij.openapi.compiler.CompilationStatusListener;

public class WhenCompilationCompletes
{
    private final ModuleSettings moduleSettings = new FakeModuleSettings("test");
    private InfinitestCore core;

    @Before
    public void inContext()
    {
        core = mock(InfinitestCore.class);
    }

    @Test
    public void shouldInvokeUpdateOnCore()
    {
        CompilationStatusListener listener = new IdeaCompilationListener(core, moduleSettings);
        listener.compilationFinished(false, 0, 0, null);

        verify(core).setRuntimeEnvironment(Matchers.any(RuntimeEnvironment.class));
        verify(core).update();
    }

    @Test
    public void shouldNotUpdatIfCompilationAborted()
    {
        CompilationStatusListener listener = new IdeaCompilationListener(core, moduleSettings);
        listener.compilationFinished(true, 0, 0, null);

        verify(core, never()).setRuntimeEnvironment(Matchers.any(RuntimeEnvironment.class));
        verify(core, never()).update();
    }

    @Test
    public void shouldNotUpdatIfCompileErrorsOccurred()
    {
        CompilationStatusListener listener = new IdeaCompilationListener(core, moduleSettings);
        listener.compilationFinished(false, 1, 0, null);

        verify(core, never()).setRuntimeEnvironment(Matchers.any(RuntimeEnvironment.class));
        verify(core, never()).update();
    }
}
