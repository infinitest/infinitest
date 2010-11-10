package org.infinitest.intellij.plugin.swingui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import junit.framework.TestCase;

public class TestDebugImage extends TestCase
{
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private DebugImage image;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        image = new DebugImage(500, 500, BACKGROUND_COLOR);
    }

    @Override
    protected void tearDown() throws Exception
    {
        try
        {
            image = null;
        }
        finally
        {
            super.tearDown();
        }
    }

    public void testInitalColor()
    {
        assertNull(image.getDrawingBounds());
        assertEquals(BACKGROUND_COLOR, image.getPixel(0, 0));
    }

    public void testDrawingBounds()
    {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        Rectangle rectangle = new Rectangle(10, 0, 50, 50);
        g.draw(rectangle);
        assertEquals(rectangle, image.getDrawingBounds());
        assertNull(image.getDrawingBounds(Color.BLUE));

        g.setColor(Color.BLUE);
        Ellipse2D.Double ellipse = new Ellipse2D.Double(20, 30, 80, 80);
        g.draw(ellipse);
        assertEquals(ellipse.getBounds(), image.getDrawingBounds(Color.BLUE));

        image.clear();
        assertNull(image.getDrawingBounds());
    }

    public void testXor()
    {
        Rectangle rectangle = new Rectangle(50, 50, 20, 40);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.draw(rectangle);
        g.setXORMode(BACKGROUND_COLOR);
        g.draw(rectangle);
        assertNull(image.getDrawingBounds());
    }

    public void testClone()
    {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        Rectangle redRectangle = new Rectangle(50, 50, 20, 40);
        g.draw(redRectangle);
        DebugImage img = image.clone();
        assertEquals(redRectangle, img.getDrawingBounds());
    }
}
