package org.infinitest.intellij.plugin.swingui;

import static java.awt.Color.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WhenDrawingProgressBar
{
    private FakeGraphics2D graphics;

    @Before
    public void inContext()
    {
        graphics = new FakeGraphics2D();
    }

    @Test
    public void shouldUseWhiteFontIfBarIsBlack()
    {
        CustomProgressBar progressBar = new CustomProgressBar();
        progressBar.setForeground(BLACK);

        progressBar.drawText(graphics);

        assertThat(graphics.getColor(), is(WHITE));
    }

    @Test
    public void shouldUseBlackFontIfBarIsGreen()
    {
        CustomProgressBar progressBar = new CustomProgressBar();
        progressBar.setForeground(GREEN);

        progressBar.drawText(graphics);

        assertThat(graphics.getColor(), is(BLACK));
    }
}
