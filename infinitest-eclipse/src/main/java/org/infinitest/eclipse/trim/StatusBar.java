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
package org.infinitest.eclipse.trim;

import static org.eclipse.swt.SWT.BORDER;
import static org.eclipse.swt.SWT.COLOR_BLACK;
import static org.eclipse.swt.SWT.COLOR_WHITE;
import static org.eclipse.swt.SWT.LEFT;
import static org.eclipse.swt.SWT.NONE;
import static org.infinitest.eclipse.util.GraphicsUtils.getColor;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.infinitest.eclipse.InfinitestPlugin;
import org.infinitest.eclipse.PluginActivationController;

public class StatusBar extends WorkbenchWindowControlContribution implements VisualStatus {
	
	/**
	 * Cache the current trim so we can 'dispose' it on demand
	 */
	private Composite composite;
	private Label statusLabel;
	private String statusString = "Infinitest is waiting for changes";
	private int backgroundColor = COLOR_BLACK;

	public StatusBar() {
		getPluginController().attachVisualStatus(this);
	}

	public StatusBar(String id) {
		super(id);
		
		getPluginController().attachVisualStatus(this);
	}

	@Override
	public void dispose() {
		if ((composite != null) && !composite.isDisposed()) {
			composite.dispose();
		}
		composite = null;
	}
	
	protected Control createControl(Composite parent) {
		// See: https://bugs.eclipse.org/bugs/show_bug.cgi?id=471313#c12
		parent.getParent().setRedraw(true);
		
		composite = new Composite(parent, NONE);
		
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);

		
		statusLabel = new Label(composite, BORDER | LEFT);
		statusLabel.setForeground(getColor(COLOR_WHITE));
		statusLabel.setText(statusString);
		statusLabel.setBackground(getColor(backgroundColor));
		
		return composite;
	}
	
	@Override
	public boolean isDynamic() {
		// See: https://bugs.eclipse.org/bugs/show_bug.cgi?id=471313#c12
		return true;
	}

	@Override
	public void setBackgroundColor(final int systemColor) {
		backgroundColor = systemColor;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				statusLabel.setBackground(getColor(systemColor));
				composite.redraw();
			}
		});
	}

	@Override
	public void setText(final String statusString) {
		this.statusString = statusString;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!statusLabel.getText().equals(statusString)) {
					statusLabel.setText(statusString);
					composite.redraw();
				}
			}
		});
	}

	@Override
	public void setToolTip(final String tooltip) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				statusLabel.setToolTipText(tooltip);
			}
		});
	}

	protected PluginActivationController getPluginController() {
		return InfinitestPlugin.getInstance().getPluginController();
	}

	@Override
	public void setTextColor(final int color) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				statusLabel.setForeground(getColor(color));
			}
		});
	}
}
