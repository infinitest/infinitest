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

import static java.awt.Color.*;
import static javax.swing.BorderFactory.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.intellij.plugin.launcher.StatusMessages.*;

import java.awt.*;
import java.awt.font.*;

import javax.swing.*;

class CustomProgressBar extends JProgressBar {
	private static final long serialVersionUID = -1L;

	private String currentTest;
	private String statusMsg;

	public CustomProgressBar() {
		setBorder(createLineBorder(getBackground(), 2));
		setBorderPainted(true);
		setMaximum(0);
		setMinimum(0);
		setFont(Font.decode("Arial-16"));
	}

	@Override
	public void addNotify() {
		super.addNotify();
		Dimension textSize = getGraphics().getFontMetrics().getStringBounds("100%", getGraphics()).getBounds().getSize();
		Dimension minSize = new Dimension(textSize.width, textSize.height + getInsetHeight());
		setPreferredSize(minSize);
		validate();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		drawBackground(g2);
		drawProgressBar(g2);
		drawText(g2);
		g2.dispose();
	}

	void drawText(Graphics2D g2) {
		if (BLACK.equals(getForeground())) {
			g2.setColor(WHITE);
		} else {
			g2.setColor(BLACK);
		}
		g2.setFont(getFont());
		TextLayout layout = new TextLayout(getStatusMessage(), getFont(), g2.getFontRenderContext());
		layout.draw(g2, 10, (getHeight() / 2) + getBaselineOffset(layout));
	}

	private float getBaselineOffset(TextLayout layout) {
		return (layout.getAscent() - layout.getDescent()) / 2;
	}

	String getStatusMessage() {
		if (statusMsg == null) {
			return getMessage(INDEXING);
		}
		String msg = statusMsg.replace("$TEST_COUNT", Integer.toString(getMaximum()));
		msg = msg.replace("$TESTS_RAN", Integer.toString(getValue()));
		msg = msg.replace("$CURRENT_TEST", getCurrentTest());
		return msg;
	}

	public String getCurrentTest() {
		if (currentTest == null) {
			return "";
		}
		return currentTest;
	}

	private void drawProgressBar(Graphics2D g2) {
		int width = (int) (getPercentComplete() * getSize().getWidth());
		int height = getSize().height;
		Rectangle rectangle = new Rectangle(0, 0, width, height);
		g2.setColor(getForeground());
		Insets insets = getInsets();
		int insetWidth = getInsetWidth();
		int insetHeight = getInsetHeight();
		g2.fillRoundRect(insets.left, insets.top, rectangle.width - insetWidth, rectangle.height - (insetHeight + 1), 15, 15);
	}

	private int getInsetWidth() {
		Insets insets = getInsets();
		return insets.left + insets.right;
	}

	private int getInsetHeight() {
		Insets insets = getInsets();
		return insets.top + insets.bottom;
	}

	private void drawBackground(Graphics2D g2) {
		Rectangle rectangle = new Rectangle(getSize());
		g2.setColor(getBackground());
		g2.fill(rectangle);
	}

	public void setCurrentTest(String testName) {
		currentTest = testName;
	}

	public void setStatusMessage(String statusMessage) {
		statusMsg = statusMessage;
	}
}
