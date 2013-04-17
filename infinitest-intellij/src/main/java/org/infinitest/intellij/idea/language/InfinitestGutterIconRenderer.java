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
package org.infinitest.intellij.idea.language;

import javax.swing.*;

import org.jetbrains.annotations.*;

import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.util.*;

class InfinitestGutterIconRenderer extends GutterIconRenderer {
	private final InnerClassFriendlyTestEvent event;

	public InfinitestGutterIconRenderer(InnerClassFriendlyTestEvent event) {
		this.event = event;
	}

	@NotNull
	@Override
	public Icon getIcon() {
		return IconLoader.getIcon("/infinitest-gutter.png");
	}

	@Override
	public String getTooltipText() {
		String message = event.getMessage();

		message = formatMessage(message);

		return event.getErrorClassName() + "(" + message + ")";
	}

	private String formatMessage(String message) {
		if (message == null) {
			return "no message";
		}
		return message.replace("<", "&lt;").replace(">", "&gt;");
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof InfinitestGutterIconRenderer) {
			return event.equals(((InfinitestGutterIconRenderer) other).event);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return event.hashCode();
	}
}
