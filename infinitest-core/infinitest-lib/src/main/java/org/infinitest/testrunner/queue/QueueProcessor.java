package org.infinitest.testrunner.queue;

import java.io.IOException;

public interface QueueProcessor
{
    void process(String string) throws InterruptedException, IOException;

    void close();

    void cleanup();
}
