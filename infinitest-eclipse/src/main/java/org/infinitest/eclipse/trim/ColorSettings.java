package org.infinitest.eclipse.trim;

import org.eclipse.swt.*;

public class ColorSettings {
	private static int failBackgroundColor = SWT.COLOR_DARK_RED;

	public static int getFailBackgroundColor() {
		return failBackgroundColor;
	}

	public static void setFailBackgroundColor(int failBackgroundColor) {
		ColorSettings.failBackgroundColor = failBackgroundColor;
	}
}
