package org.infinitest.parser;

import java.io.File;
import java.io.IOException;

interface ClassParser
{
    JavaClass getClass(String className);

    JavaClass parse(File file) throws IOException;

    void clear();
}