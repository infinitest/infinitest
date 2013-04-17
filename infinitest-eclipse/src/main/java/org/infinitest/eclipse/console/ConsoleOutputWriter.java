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
package org.infinitest.eclipse.console;

import static org.eclipse.jface.resource.ImageDescriptor.*;

import java.io.*;

import org.eclipse.ui.console.*;

// RISK Untested
public class ConsoleOutputWriter implements TextOutputWriter {
	private MessageConsole console;

	@Override
	public void activate() {
		getConsole().activate();
	}

	@Override
	public void appendText(String newText) {
		try {
			MessageConsoleStream stream = getConsole().newMessageStream();
			stream.print(newText);
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private MessageConsole getConsole() {
		if (console == null) {
			ConsolePlugin plugin = ConsolePlugin.getDefault();
			IConsoleManager conMan = plugin.getConsoleManager();
			console = new MessageConsole("Infinitest Console", getMissingImageDescriptor());
			console.getDocument().set("");
			conMan.addConsoles(new IConsole[] { console });
		}

		return console;
	}

	@Override
	public void clearConsole() {
		getConsole().clearConsole();
	}
}
