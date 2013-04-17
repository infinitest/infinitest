/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.intellij.plugin.swingui;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.text.*;
import java.util.*;

public class FakeGraphics2D extends Graphics2D {
	private Color color;

	@Override
	public void draw(Shape shape) {
	}

	@Override
	public boolean drawImage(Image image, AffineTransform affineTransform, ImageObserver imageObserver) {
		return false;
	}

	@Override
	public void drawImage(BufferedImage bufferedImage, BufferedImageOp bufferedImageOp, int i, int i1) {
	}

	@Override
	public void drawRenderedImage(RenderedImage renderedImage, AffineTransform affineTransform) {
	}

	@Override
	public void drawRenderableImage(RenderableImage renderableImage, AffineTransform affineTransform) {
	}

	@Override
	public void drawString(String s, int i, int i1) {
	}

	@Override
	public void drawString(String s, float v, float v1) {
	}

	@Override
	public void drawString(AttributedCharacterIterator attributedCharacterIterator, int i, int i1) {
	}

	@Override
	public boolean drawImage(Image image, int i, int i1, ImageObserver imageObserver) {
		return false;
	}

	@Override
	public boolean drawImage(Image image, int i, int i1, int i2, int i3, ImageObserver imageObserver) {
		return false;
	}

	@Override
	public boolean drawImage(Image image, int i, int i1, Color color, ImageObserver imageObserver) {
		return false;
	}

	@Override
	public boolean drawImage(Image image, int i, int i1, int i2, int i3, Color color, ImageObserver imageObserver) {
		return false;
	}

	@Override
	public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, ImageObserver imageObserver) {
		return false;
	}

	@Override
	public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Color color, ImageObserver imageObserver) {
		return false;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void drawString(AttributedCharacterIterator attributedCharacterIterator, float v, float v1) {
	}

	@Override
	public void drawGlyphVector(GlyphVector glyphVector, float v, float v1) {
	}

	@Override
	public void fill(Shape shape) {
	}

	@Override
	public boolean hit(Rectangle rectangle, Shape shape, boolean b) {
		return false;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return null;
	}

	@Override
	public void setComposite(Composite composite) {
	}

	@Override
	public void setPaint(Paint paint) {
	}

	@Override
	public void setStroke(Stroke stroke) {
	}

	@Override
	public void setRenderingHint(RenderingHints.Key key, Object o) {
	}

	@Override
	public Object getRenderingHint(RenderingHints.Key key) {
		return null;
	}

	@Override
	public void setRenderingHints(Map<?, ?> map) {
	}

	@Override
	public void addRenderingHints(Map<?, ?> map) {
	}

	@Override
	public RenderingHints getRenderingHints() {
		return null;
	}

	@Override
	public Graphics create() {
		return null;
	}

	@Override
	public void translate(int i, int i1) {
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void setPaintMode() {
	}

	@Override
	public void setXORMode(Color color) {
	}

	@Override
	public Font getFont() {
		return null;
	}

	@Override
	public void setFont(Font font) {
	}

	@Override
	public FontMetrics getFontMetrics(Font font) {
		return null;
	}

	@Override
	public Rectangle getClipBounds() {
		return null;
	}

	@Override
	public void clipRect(int i, int i1, int i2, int i3) {
	}

	@Override
	public void setClip(int i, int i1, int i2, int i3) {
	}

	@Override
	public Shape getClip() {
		return null;
	}

	@Override
	public void setClip(Shape shape) {
	}

	@Override
	public void copyArea(int i, int i1, int i2, int i3, int i4, int i5) {
	}

	@Override
	public void drawLine(int i, int i1, int i2, int i3) {
	}

	@Override
	public void fillRect(int i, int i1, int i2, int i3) {
	}

	@Override
	public void clearRect(int i, int i1, int i2, int i3) {
	}

	@Override
	public void drawRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {
	}

	@Override
	public void fillRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {
	}

	@Override
	public void drawOval(int i, int i1, int i2, int i3) {
	}

	@Override
	public void fillOval(int i, int i1, int i2, int i3) {
	}

	@Override
	public void drawArc(int i, int i1, int i2, int i3, int i4, int i5) {
	}

	@Override
	public void fillArc(int i, int i1, int i2, int i3, int i4, int i5) {
	}

	@Override
	public void drawPolyline(int[] ints, int[] ints1, int i) {
	}

	@Override
	public void drawPolygon(int[] ints, int[] ints1, int i) {
	}

	@Override
	public void fillPolygon(int[] ints, int[] ints1, int i) {
	}

	@Override
	public void translate(double v, double v1) {
	}

	@Override
	public void rotate(double v) {
	}

	@Override
	public void rotate(double v, double v1, double v2) {
	}

	@Override
	public void scale(double v, double v1) {
	}

	@Override
	public void shear(double v, double v1) {
	}

	@Override
	public void transform(AffineTransform affineTransform) {
	}

	@Override
	public void setTransform(AffineTransform affineTransform) {
	}

	@Override
	public AffineTransform getTransform() {
		return null;
	}

	@Override
	public Paint getPaint() {
		return null;
	}

	@Override
	public Composite getComposite() {
		return null;
	}

	@Override
	public void setBackground(Color color) {
	}

	@Override
	public Color getBackground() {
		return null;
	}

	@Override
	public Stroke getStroke() {
		return null;
	}

	@Override
	public void clip(Shape shape) {
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return new FontRenderContext(null, false, false);
	}
}
