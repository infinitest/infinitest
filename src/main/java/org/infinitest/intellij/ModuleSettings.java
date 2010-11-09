package org.infinitest.intellij;

import org.apache.log4j.Logger;
import org.infinitest.RuntimeEnvironment;

public interface ModuleSettings
{
    void writeToLogger(Logger log);

    String getName();

    RuntimeEnvironment getRuntimeEnvironment();
}
