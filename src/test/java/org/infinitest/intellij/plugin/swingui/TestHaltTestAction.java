package org.infinitest.intellij.plugin.swingui;

import static org.junit.Assert.*;

import javax.swing.Action;
import javax.swing.Icon;

import org.infinitest.TestControl;
import org.junit.Test;

public class TestHaltTestAction
{
    private boolean runTests;

    @Test
    public void shouldDisableAndEnableTests()
    {
        TestControl control = new TestControl()
        {
            public void setRunTests(boolean shouldRunTests)
            {
                runTests = shouldRunTests;
            }

            public boolean shouldRunTests()
            {
                return runTests;
            }
        };
        runTests = true;
        HaltTestAction action = new HaltTestAction(control);
        Icon startIcon = (Icon) action.getValue(Action.SMALL_ICON);
        action.actionPerformed(null);
        assertFalse(runTests);
        assertNotSame(startIcon, action.getValue(Action.SMALL_ICON));

        action.actionPerformed(null);
        assertTrue(runTests);
        assertSame(startIcon, action.getValue(Action.SMALL_ICON));
    }
}
