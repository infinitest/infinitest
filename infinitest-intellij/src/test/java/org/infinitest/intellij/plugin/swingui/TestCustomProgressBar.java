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

import static org.hamcrest.Matchers.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.*;
import static org.infinitest.intellij.plugin.launcher.StatusMessages.*;
import static org.junit.Assert.*;

import java.awt.*;
import java.awt.image.*;

import org.infinitest.*;
import org.junit.*;

public class TestCustomProgressBar {
	private CustomProgressBar progressBar;

	@Before
	public void inContext() {
		progressBar = new CustomProgressBar();
		progressBar.setForeground(FAILING_COLOR);
		progressBar.setMaximum(100);
		progressBar.setValue(50);
	}

	@After
	public void cleanupContext() {
		progressBar = null;
	}

	@Test
	public void shouldUseACustomPainter() {
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
	public void shouldUseVariableReplacementToPopulateStatusMessage() {
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
	public void shouldReplaceAllVariablesInCurrentStatusMessages() {
		CoreStatus[] statuses = new CoreStatus[]{SCANNING, INDEXING, RUNNING, PASSING, FAILING};
		for (CoreStatus status : statuses) {
			progressBar.setStatusMessage(getMessage(status));
			assertThat(progressBar.getStatusMessage(), not(containsString("$")));
		}
	}
}
