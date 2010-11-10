package org.infinitest.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class IconTest
{
    @Test
    public void shouldProvideStreamsForAllIcons()
    {
        for (Icon each : Icon.values())
        {
            assertNotNull(each.name(), each.getLargeFormatStream());
            assertNotNull(each.name(), each.getSmallFormatStream());
        }
    }
}
