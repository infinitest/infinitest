package org.infinitest.eclipse.resolution;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhenTheUserClicksOffTheFailureViewer
{
    @Test
    public void shouldDisposeOfTheDialog()
    {
        FakeShell dialog = new FakeShell();
        DialogDeactivationDisposer disposer = new DialogDeactivationDisposer(dialog);
        disposer.shellDeactivated(null);
        assertTrue(dialog.disposed);
    }
}
