package org.infinitest.eclipse.util;

import static com.google.common.collect.Maps.*;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.infinitest.util.Icon;

public class IconCache
{
    private static Map<Icon, Image> smallImages = newHashMap();
    private static Map<Icon, Image> largeImages = newHashMap();

    public static synchronized Image getSmallImage(Icon imageType)
    {
        if (!smallImages.containsKey(imageType))
        {
            smallImages.put(imageType, createImage(imageType.getSmallFormatStream()));
        }
        return smallImages.get(imageType);
    }

    public static synchronized Image getLargeImage(Icon imageName)
    {
        if (!largeImages.containsKey(imageName))
        {
            largeImages.put(imageName, createImage(imageName.getLargeFormatStream()));
        }
        return largeImages.get(imageName);
    }

    private static Image createImage(InputStream iconStream)
    {
        if (iconStream != null)
        {
            return new Image(Display.getCurrent(), iconStream);
        }
        return null;
    }
}
