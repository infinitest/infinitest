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
package org.infinitest.intellij.idea.window;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.infinitest.CoreStatus;
import org.infinitest.ResultCollector;
import org.infinitest.intellij.IntellijMockBase;
import org.junit.Test;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.ContentManager;

public class InfinitestToolWindowFactoryTest extends IntellijMockBase {
	@Test
	public void windowInitialization() {
		IntellijMockBase.setupApplication();
		InfinitestToolWindowFactory factory = new InfinitestToolWindowFactory();
		
		ToolWindow toolWindow = mock(ToolWindow.class);
		ContentManager contentManager = mock(ContentManager.class);
		
		when(toolWindow.getContentManager()).thenReturn(contentManager);
		
		ResultCollector resultCollector = mock(ResultCollector.class);
		
		when(launcher.getResultCollector()).thenReturn(resultCollector);
		
		when(resultCollector.getStatus()).thenReturn(CoreStatus.SCANNING);
		
		factory.createToolWindowContent(project, toolWindow);
		
		verify(toolWindow).setIcon(any());
	}
}
