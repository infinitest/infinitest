package org.infinitest.intellij.idea.language;

import static org.hamcrest.Matchers.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WhenFormattingTooltipText
{
    private static final RuntimeException ERROR = new RuntimeException((String) null);
    private InfinitestGutterIconRenderer renderer;

    @Before
    public void setUp()
    {
        renderer = new InfinitestGutterIconRenderer(new InnerClassFriendlyTestEvent(methodFailed(null, "", ERROR)));
    }

    @Test
    public void shouldIncludeExceptionType()
    {
        assertThat(renderer.getTooltipText(), containsString("RuntimeException"));
    }

    @Test
    public void shouldReplaceNullMessageWithNullString()
    {
        assertThat(renderer.getTooltipText(), containsString("no message"));
    }
}
