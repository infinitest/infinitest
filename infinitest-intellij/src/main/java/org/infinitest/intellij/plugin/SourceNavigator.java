package org.infinitest.intellij.plugin;

public interface SourceNavigator
{
    /**
     * Opens the specified class
     * 
     * @param className
     *            FQCN
     * @return SourceNavigator
     */
    SourceNavigator open(String className);

    /**
     * Navigates to the specified line number
     * 
     * @param line
     *            1-based line number to open
     */
    void line(int line);
}
