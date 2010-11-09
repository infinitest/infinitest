package org.infinitest.intellij.plugin.swingui;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.GraphicsConfiguration;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.FontRenderContext;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class FakeGraphics2D extends Graphics2D
{
    private Color color;

    public void draw(Shape shape)
    {
    }

    public boolean drawImage(Image image, AffineTransform affineTransform, ImageObserver imageObserver)
    {
        return false;
    }

    public void drawImage(BufferedImage bufferedImage, BufferedImageOp bufferedImageOp, int i, int i1)
    {
    }

    public void drawRenderedImage(RenderedImage renderedImage, AffineTransform affineTransform)
    {
    }

    public void drawRenderableImage(RenderableImage renderableImage, AffineTransform affineTransform)
    {
    }

    public void drawString(String s, int i, int i1)
    {
    }

    public void drawString(String s, float v, float v1)
    {
    }

    public void drawString(AttributedCharacterIterator attributedCharacterIterator, int i, int i1)
    {
    }

    public boolean drawImage(Image image, int i, int i1, ImageObserver imageObserver)
    {
        return false;
    }

    public boolean drawImage(Image image, int i, int i1, int i2, int i3, ImageObserver imageObserver)
    {
        return false;
    }

    public boolean drawImage(Image image, int i, int i1, Color color, ImageObserver imageObserver)
    {
        return false;
    }

    public boolean drawImage(Image image, int i, int i1, int i2, int i3, Color color, ImageObserver imageObserver)
    {
        return false;
    }

    public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, ImageObserver imageObserver)
    {
        return false;
    }

    public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Color color, ImageObserver imageObserver)
    {
        return false;
    }

    public void dispose()
    {
    }

    public void drawString(AttributedCharacterIterator attributedCharacterIterator, float v, float v1)
    {
    }

    public void drawGlyphVector(GlyphVector glyphVector, float v, float v1)
    {
    }

    public void fill(Shape shape)
    {
    }

    public boolean hit(Rectangle rectangle, Shape shape, boolean b)
    {
        return false;
    }

    public GraphicsConfiguration getDeviceConfiguration()
    {
        return null;
    }

    public void setComposite(Composite composite)
    {
    }

    public void setPaint(Paint paint)
    {
    }

    public void setStroke(Stroke stroke)
    {
    }

    public void setRenderingHint(RenderingHints.Key key, Object o)
    {
    }

    public Object getRenderingHint(RenderingHints.Key key)
    {
        return null;
    }

    public void setRenderingHints(Map<?, ?> map)
    {
    }

    public void addRenderingHints(Map<?, ?> map)
    {
    }

    public RenderingHints getRenderingHints()
    {
        return null;
    }

    public Graphics create()
    {
        return null;
    }

    public void translate(int i, int i1)
    {
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public void setPaintMode()
    {
    }

    public void setXORMode(Color color)
    {
    }

    public Font getFont()
    {
        return null;
    }

    public void setFont(Font font)
    {
    }

    public FontMetrics getFontMetrics(Font font)
    {
        return null;
    }

    public Rectangle getClipBounds()
    {
        return null;
    }

    public void clipRect(int i, int i1, int i2, int i3)
    {
    }

    public void setClip(int i, int i1, int i2, int i3)
    {
    }

    public Shape getClip()
    {
        return null;
    }

    public void setClip(Shape shape)
    {
    }

    public void copyArea(int i, int i1, int i2, int i3, int i4, int i5)
    {
    }

    public void drawLine(int i, int i1, int i2, int i3)
    {
    }

    public void fillRect(int i, int i1, int i2, int i3)
    {
    }

    public void clearRect(int i, int i1, int i2, int i3)
    {
    }

    public void drawRoundRect(int i, int i1, int i2, int i3, int i4, int i5)
    {
    }

    public void fillRoundRect(int i, int i1, int i2, int i3, int i4, int i5)
    {
    }

    public void drawOval(int i, int i1, int i2, int i3)
    {
    }

    public void fillOval(int i, int i1, int i2, int i3)
    {
    }

    public void drawArc(int i, int i1, int i2, int i3, int i4, int i5)
    {
    }

    public void fillArc(int i, int i1, int i2, int i3, int i4, int i5)
    {
    }

    public void drawPolyline(int[] ints, int[] ints1, int i)
    {
    }

    public void drawPolygon(int[] ints, int[] ints1, int i)
    {
    }

    public void fillPolygon(int[] ints, int[] ints1, int i)
    {
    }

    public void translate(double v, double v1)
    {
    }

    public void rotate(double v)
    {
    }

    public void rotate(double v, double v1, double v2)
    {
    }

    public void scale(double v, double v1)
    {
    }

    public void shear(double v, double v1)
    {
    }

    public void transform(AffineTransform affineTransform)
    {
    }

    public void setTransform(AffineTransform affineTransform)
    {
    }

    public AffineTransform getTransform()
    {
        return null;
    }

    public Paint getPaint()
    {
        return null;
    }

    public Composite getComposite()
    {
        return null;
    }

    public void setBackground(Color color)
    {
    }

    public Color getBackground()
    {
        return null;
    }

    public Stroke getStroke()
    {
        return null;
    }

    public void clip(Shape shape)
    {
    }

    public FontRenderContext getFontRenderContext()
    {
        return new FontRenderContext(null, false, false);
    }
}
