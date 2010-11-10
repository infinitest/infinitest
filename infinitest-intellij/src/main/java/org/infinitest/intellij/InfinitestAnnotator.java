package org.infinitest.intellij;

import org.infinitest.testrunner.TestEvent;

public interface InfinitestAnnotator
{
    void annotate(TestEvent event);

    void clearAnnotation(TestEvent event);
}
