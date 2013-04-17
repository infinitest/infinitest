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

import junit.framework.*;

public class TestDebugImage extends TestCase {
	private static final Color BACKGROUND_COLOR = Color.BLACK;
	private DebugImage image;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		image = new DebugImage(500, 500, BACKGROUND_COLOR);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			image = null;
		} finally {
			super.tearDown();
		}
	}

	public void testInitalColor() {
		assertNull(image.getDrawingBounds());
		assertEquals(BACKGROUND_COLOR, image.getPixel(0, 0));
	}

	public void testXor() {
		Rectangle rectangle = new Rectangle(50, 50, 20, 40);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.RED);
		g.draw(rectangle);
		g.setXORMode(BACKGROUND_COLOR);
		g.draw(rectangle);
		assertNull(image.getDrawingBounds());
	}

	public void testClone() {
		Graphics2D g = image.createGraphics();
		g.setColor(Color.RED);
		Rectangle redRectangle = new Rectangle(50, 50, 20, 40);
		g.draw(redRectangle);
		DebugImage img = image.clone();
		assertEquals(redRectangle, img.getDrawingBounds());
	}
}
