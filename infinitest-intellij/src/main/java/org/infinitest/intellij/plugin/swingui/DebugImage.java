package org.infinitest.intellij.plugin.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class DebugImage extends BufferedImage implements Cloneable
{
    private final Color bgColor;

    public DebugImage(int width, int height, Color backgroundColor)
    {
        this(width, height, TYPE_4BYTE_ABGR, backgroundColor);
    }

    public DebugImage(int width, int height, int imageType, Color backgroundColor)
    {
        super(width, height, imageType);
        bgColor = backgroundColor;
        clear();
    }

    public DebugImage(Dimension size, Color backgroundColor)
    {
        this(size.width, size.height, backgroundColor);
    }

    /**
     * Resets the image to be the backgound color
     */
    public void clear()
    {
        Graphics g = getGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();
    }

    /**
     * Dumps the current image to a PNG file.
     *
     * @param file
     *            File
     * @throws IOException
     */
    public void dumpImageToFile(File file) throws IOException
    {
        ImageIO.write(this, "PNG", file);
    }

    public void dumpImageToFile(String fileName) throws IOException
    {
        dumpImageToFile(new File(fileName));
    }

    /**
     * Returns the bounds of the shapes that were drawn using a color other than the background
     * color, or null if nothing was drawn.
     *
     * @return Rectangle
     */
    public Rectangle getDrawingBounds()
    {
        return getDrawingBounds(null);
    }

    /**
     * Returns the bounds of the shapes that were drawn using the given color, or null if nothing
     * was drawn.
     *
     * @return Rectangle
     */
    public Rectangle getDrawingBounds(Color color)
    {
        try
        {
            int left = findLeft(color);
            int right = findRight(color);
            int top = findTop(color);
            int bottom = findBottom(color);
            return new Rectangle(left, top, right - left, bottom - top);
        }
        catch (NoRenderingException ex)
        {
            // Nothing was drawn, return null.
            return null;
        }
    }

    /**
     * Returns true if the given pixel is a match for the given color or, if the color is null, if
     * the given pixel is simply not a background pixel.
     *
     * @param color
     * @param x
     * @param y
     * @return
     */
    private boolean colorMatch(Color color, int x, int y)
    {
        Color pixel = getPixel(x, y);
        return !pixel.equals(bgColor) && (color == null || pixel.equals(color));
    }

    private int findLeft(Color color) throws NoRenderingException
    {
        for (int x = 0; x < getWidth(); x++)
            for (int y = 0; y < getHeight(); y++)
                if (colorMatch(color, x, y))
                    return x;
        throw new NoRenderingException();
    }

    private int findTop(Color color) throws NoRenderingException
    {
        for (int y = 0; y < getHeight(); y++)
            for (int x = 0; x < getWidth(); x++)
                if (colorMatch(color, x, y))
                    return y;
        throw new NoRenderingException();
    }

    private int findBottom(Color color) throws NoRenderingException
    {
        for (int y = getHeight() - 1; y >= 0; y--)
            for (int x = 0; x < getWidth(); x++)
                if (colorMatch(color, x, y))
                    return y;
        throw new NoRenderingException();
    }

    private int findRight(Color color) throws NoRenderingException
    {
        for (int x = getWidth() - 1; x >= 0; x--)
            for (int y = 0; y < getHeight(); y++)
                if (colorMatch(color, x, y))
                    return x;
        throw new NoRenderingException();
    }

    private class NoRenderingException extends Exception
    {
        private static final long serialVersionUID = -1L;

        public NoRenderingException()
        {
            super("Nothing was drawn in this render context.");
        }
    }

    public Color getPixel(int x, int y)
    {
        return new Color(getRGB(x, y));
    }

    @Override
    public DebugImage clone()
    {
        DebugImage clone = new DebugImage(getWidth(), getHeight(), getType(), bgColor);
        Graphics2D g = clone.createGraphics();
        g.drawImage(this, 0, 0, null);
        return clone;
    }
}
