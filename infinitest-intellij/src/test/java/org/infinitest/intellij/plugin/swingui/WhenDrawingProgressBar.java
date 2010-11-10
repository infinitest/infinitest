package org.infinitest.intellij.plugin.swingui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

import static java.awt.Color.BLACK;
import static java.awt.Color.GREEN;
import static java.awt.Color.WHITE;

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
