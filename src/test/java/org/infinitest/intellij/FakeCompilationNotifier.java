package org.infinitest.intellij;

import com.intellij.openapi.compiler.CompilationStatusListener;

@SuppressWarnings("all")
public class FakeCompilationNotifier implements CompilationNotifier
{
    public void addCompilationStatusListener(CompilationStatusListener compilationListener)
    {
    }
}
