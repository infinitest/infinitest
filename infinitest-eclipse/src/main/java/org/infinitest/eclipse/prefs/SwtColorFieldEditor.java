package org.infinitest.eclipse.prefs;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class SwtColorFieldEditor extends ComboFieldEditor {

	private static final String[][] ENTRY_NAMES_AND_VALUES = {
		{"Black", String.valueOf(SWT.COLOR_BLACK)},
		{"Blue", String.valueOf(SWT.COLOR_BLUE )},
		{"Blue (Dark)", String.valueOf(SWT.COLOR_DARK_BLUE)},
		{"Cyan", String.valueOf(SWT.COLOR_CYAN)},
		{"Cyan(Dark)", String.valueOf(SWT.COLOR_DARK_CYAN)},
		{"Green", String.valueOf(SWT.COLOR_GREEN)},
		{"Green (Dark)", String.valueOf(SWT.COLOR_DARK_GREEN)},
		{"Gray", String.valueOf(SWT.COLOR_GRAY)},
		{"Gray (Dark)", String.valueOf(SWT.COLOR_DARK_GRAY)},
		{"Red", String.valueOf(SWT.COLOR_RED)},
		{"Red (Dark)", String.valueOf(SWT.COLOR_DARK_RED)},
		{"Magenta", String.valueOf(SWT.COLOR_MAGENTA)},
		{"Magenta (Dark)", String.valueOf(SWT.COLOR_DARK_MAGENTA)},
		{"Yellow", String.valueOf(SWT.COLOR_YELLOW)},
		{"Yellow (Dark)", String.valueOf(SWT.COLOR_DARK_YELLOW)},
		{"White", String.valueOf(SWT.COLOR_WHITE)},
	};

	public SwtColorFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, ENTRY_NAMES_AND_VALUES, parent);
		// TODO Auto-generated constructor stub
	}

}
