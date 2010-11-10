package org.infinitest.intellij;

import com.intellij.openapi.compiler.CompilationStatusListener;

public interface CompilationNotifier
{
    void addCompilationStatusListener(CompilationStatusListener compilationListener);
}
