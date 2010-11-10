package org.infinitest.parser;

import java.io.File;
import java.util.Collection;

public interface JavaClass
{
    String getName();

    /**
     * Gets the collection on classes that this class depends on. i.e. the list of this classes
     * children.
     */
    Collection<String> getImports();

    /**
     * Clean up any unnecessary references to save memory
     */
    void dispose();

    boolean isATest();

    boolean locatedInClassFile();

    File getClassFile();
}