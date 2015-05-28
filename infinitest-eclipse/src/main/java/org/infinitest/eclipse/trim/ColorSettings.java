package org.infinitest.eclipse.trim;

import org.eclipse.swt.*;

public class ColorSettings {
	private static int failBackgroundColor = SWT.COLOR_DARK_RED;
	private static int failTextColor = SWT.COLOR_WHITE;

	public static int getFailTextColor() {
		return failTextColor;
	}

	public static void setFailTextColor(int failTextColor) {
		ColorSettings.failTextColor = failTextColor;
	}

	public static int getFailBackgroundColor() {
		return failBackgroundColor;
	}

	public static void setFailBackgroundColor(int failBackgroundColor) {
		ColorSettings.failBackgroundColor = failBackgroundColor;
	}

}
