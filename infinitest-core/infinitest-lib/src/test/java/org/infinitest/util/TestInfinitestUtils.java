package org.infinitest.util;

import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestInfinitestUtils
{
    @Test
    public void shouldFormatTimeAsHHMMSS()
    {
        assertEquals("00:00:20", InfinitestUtils.formatTime(20 * 1000));
    }

    @Test
    public void shouldAddLineFeedsToErrorMessagesWithAPath()
    {
        String actualMessage = getExceptionMessage(new IllegalArgumentException("path:path2:path3"));
        assertEquals("path:\npath2:\npath3", actualMessage);
    }

    @Test
    public void shouldProvideShortClassNameForTest()
    {
        assertEquals("TestFoo", stripPackageName("com.foobar.TestFoo"));
    }

    @Test
    public void shouldTruncateLongErrorMessages()
    {
        String actualMessage = getExceptionMessage(new IllegalArgumentException(
                        "This is a really long error message that needs to be truncated to a reasonable length."));
        assertEquals("This is a really long error message that needs to ...", actualMessage);
    }

    @Test
    public void shouldUseClassNameWhenMessageIsNull()
    {
        String actualMessage = getExceptionMessage(new NullPointerException());
        assertEquals(NullPointerException.class.getName(), actualMessage);
    }

    @Test
    public void shouldAlwaysUseForwardSlashForResourcePath()
    {
        assertFalse(getResourceName(TestInfinitestUtils.class.getName()).contains("\\"));
    }
}
