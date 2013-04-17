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

import static org.eclipse.swt.SWT.*;
import static org.infinitest.eclipse.util.GraphicsUtils.*;

import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.menus.*;
import org.infinitest.eclipse.*;

public class StatusBar extends AbstractWorkbenchTrimWidget implements VisualStatus {
	/**
	 * Cache the current trim so we can 'dispose' it on demand
	 */
	private Composite composite;
	private Label statusLabel;
	private String statusString = "Infinitest is waiting for changes";
	private int backgroundColor = COLOR_BLACK;

	@Override
	public void init(IWorkbenchWindow workbenchWindow) {
		getPluginController().attachVisualStatus(this);
	}

	@Override
	public void dispose() {
		if ((composite != null) && !composite.isDisposed()) {
			composite.dispose();
		}
		composite = null;
	}

	@Override
	public void fill(Composite parent, int oldSide, int newSide) {
		composite = new Composite(parent, NONE);

		RowLayout layout = new RowLayout();
		layout.marginHeight = 4;
		layout.marginWidth = 2;
		layout.center = true;
		composite.setLayout(layout);

		addStatusLabel(newSide);
		composite.layout();
	}

	private void addStatusLabel(int newSide) {
		statusLabel = new Label(composite, BORDER | LEFT);
		statusLabel.setLayoutData(getRowData(newSide));
		statusLabel.setForeground(getColor(COLOR_WHITE));
		statusLabel.setText(statusString);
		statusLabel.setBackground(getColor(backgroundColor));
	}

	RowData getRowData(int newSide) {
		RowData rowData = new RowData();
		if ((newSide == BOTTOM) || (newSide == TOP)) {
			rowData.width = 400;
		}
		return rowData;
	}

	public void setBackgroundColor(final int systemColor) {
		backgroundColor = systemColor;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				statusLabel.setBackground(getColor(systemColor));
				composite.redraw();
			}
		});
	}

	public void setText(final String statusString) {
		this.statusString = statusString;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!statusLabel.getText().equals(statusString)) {
					statusLabel.setText(statusString);
					composite.redraw();
				}
			}
		});
	}

	public void setToolTip(final String tooltip) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				statusLabel.setToolTipText(tooltip);
			}
		});
	}

	protected PluginActivationController getPluginController() {
		return InfinitestPlugin.getInstance().getPluginController();
	}

	public void setTextColor(final int color) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				statusLabel.setForeground(getColor(color));
			}
		});
	}
}
