/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
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
}
