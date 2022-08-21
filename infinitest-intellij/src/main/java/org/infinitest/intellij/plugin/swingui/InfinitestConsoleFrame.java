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

import java.nio.charset.Charset;

import javax.swing.JComponent;

import org.infinitest.ConsoleOutputListener;
import org.infinitest.ConsoleOutputListener.OutputType;
import org.jetbrains.annotations.NotNull;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;

public class InfinitestConsoleFrame implements ConsoleOutputListener {
	private ConsoleView consoleView;
	
	public InfinitestConsoleFrame(Project project) {
		consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
	}
	
	public JComponent getComponent() {
		return consoleView.getComponent();
	}

	@Override
	public void consoleOutputUpdate(String newText, OutputType outputType) {
		@NotNull
		ConsoleViewContentType consoleViewContentType;
		switch (outputType) {
		case STDOUT :
			consoleViewContentType = ConsoleViewContentType.NORMAL_OUTPUT;
			break;
		case STDERR:
			consoleViewContentType = ConsoleViewContentType.ERROR_OUTPUT;
			break;
		default:
			throw new IllegalArgumentException("Unknown output type: " + outputType);
		}
		
		consoleView.print(newText, consoleViewContentType);
	}
}
