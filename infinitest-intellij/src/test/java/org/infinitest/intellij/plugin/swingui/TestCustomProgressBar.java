package org.infinitest.intellij.plugin.swingui;

import static org.hamcrest.Matchers.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.*;
import static org.infinitest.intellij.plugin.launcher.StatusMessages.*;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.infinitest.CoreStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCustomProgressBar
{
    private CustomProgressBar progressBar;

    @Before
    public void inContext()
    {
        progressBar = new CustomProgressBar();
        progressBar.setForeground(FAILING_COLOR);
        progressBar.setMaximum(100);
        progressBar.setValue(50);
    }

    @After
    public void cleanupContext()
    {
        progressBar = null;
    }

    @Test
    public void shouldDisplayCurrentlyRunningTest()
    {
        Dimension size = new Dimension(250, 30);
        progressBar.setSize(size);
        DebugImage img = new DebugImage(progressBar.getSize(), Color.WHITE);
        progressBar.paint(img.getGraphics());
        Rectangle textBoundsForPercentComplete = new Rectangle(19, 8, 5, 3);
        assertNotNull(img.getDrawingBounds(Color.BLACK));

        progressBar.setCurrentTest("com.foo.TestMyClass");
        img.clear();
        progressBar.paint(img.getGraphics());
        Rectangle textBoundsForNewText = new Rectangle(textBoundsForPercentComplete);
        textBoundsForNewText.setSize(126, 8);
        assertNotNull(img.getDrawingBounds(Color.BLACK));
    }

    @Test
    public void shouldUseACustomPainter()
    {
        progressBar.setValue(40);
        Dimension size = new Dimension(1000, 30);
        progressBar.setSize(size);
        BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = img.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, size.width, size.height);
        progressBar.paint(graphics);
        // ImageIO.write(img, "PNG", new File("customPainter.png"));
        int progressBarRightEdge = 400;
        Color foregroundColor = progressBar.getForeground();
        assertEquals(foregroundColor, new Color(img.getRGB(progressBarRightEdge - 5, size.height / 2)));
        Color backgroundColor = new Color(img.getRGB(progressBarRightEdge + 5, size.height / 2));
        assertFalse(foregroundColor.equals(backgroundColor));
        assertFalse(Color.WHITE.equals(backgroundColor));
    }

    @Test
    public void shouldUseVariableReplacementToPopulateStatusMessage()
    {
        assertEquals(getMessage(INDEXING), progressBar.getStatusMessage());

        progressBar.setStatusMessage("Scanning for tests");
        assertEquals("Scanning for tests", progressBar.getStatusMessage());

        progressBar.setStatusMessage("Ran $TEST_COUNT tests");
        progressBar.setMaximum(52);
        assertEquals("Ran 52 tests", progressBar.getStatusMessage());

        progressBar.setStatusMessage("Running $TESTS_RAN of $TEST_COUNT - $CURRENT_TEST");
        progressBar.setMaximum(52);
        progressBar.setValue(31);
        progressBar.setCurrentTest("com.foo.MyTest");
        assertEquals("Running 31 of 52 - com.foo.MyTest", progressBar.getStatusMessage());
    }

    @Test
    public void shouldReplaceAllVariablesInCurrentStatusMessages()
    {
        CoreStatus[] statuses = new CoreStatus[] { SCANNING, INDEXING, RUNNING, PASSING, FAILING };
        for (CoreStatus status : statuses)
        {
            progressBar.setStatusMessage(getMessage(status));
            assertThat(progressBar.getStatusMessage(), not(containsString("$")));
        }
    }
}
