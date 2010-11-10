package org.infinitest.intellij;

import org.infinitest.TestControl;

@SuppressWarnings("all")
public class FakeTestControl implements TestControl
{
    public void setRunTests(boolean shouldRunTests)
    {
    }

    public boolean shouldRunTests()
    {
        return false;
    }
}
