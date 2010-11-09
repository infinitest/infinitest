package org.infinitest.eclipse.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public abstract class GraphicsUtils
{
    public static Color getColor(int color)
    {
        return Display.getCurrent().getSystemColor(color);
    }
}
