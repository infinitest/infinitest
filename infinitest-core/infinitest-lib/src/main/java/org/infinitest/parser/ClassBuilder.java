package org.infinitest.parser;

import java.io.File;

interface ClassBuilder
{
    JavaClass createClass(String classname);

    JavaClass loadClass(File file);

    void clear();
}