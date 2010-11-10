package org.infinitest.eclipse.trim;

import static org.easymock.EasyMock.*;
import static org.eclipse.swt.SWT.*;
import static org.junit.Assert.*;

import org.eclipse.swt.layout.RowData;
import org.infinitest.eclipse.PluginActivationController;
import org.junit.Before;
import org.junit.Test;

public class StatusBarTest
{
    private StatusBar statusBar;

    @Before
    public void inContext()
    {
        statusBar = new StatusBar();
    }

    @Test
    public void shouldShrinkWhenMovedToSideBars()
    {
        RowData rowData = statusBar.getRowData(BOTTOM);
        assertEquals(400, rowData.width);

        rowData = statusBar.getRowData(LEFT);
        assertEquals(-1, rowData.width);
    }

    @Test
    public void shouldRegisterStatusPluginController()
    {
        final PluginActivationController mockController = createMock(PluginActivationController.class);
        mockController.attachVisualStatus(isA(StatusBar.class));
        replay(mockController);
        StatusBar statusBar = new StatusBar()
        {
            @Override
            protected PluginActivationController getPluginController()
            {
                return mockController;
            }
        };
        statusBar.init(null);

        verify(mockController);
    }
}
