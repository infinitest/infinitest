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
package org.infinitest.eclipse.prefs;

import static java.lang.Integer.MAX_VALUE;
import static org.infinitest.eclipse.prefs.PreferencesConstants.AUTO_TEST;
import static org.infinitest.eclipse.prefs.PreferencesConstants.PARALLEL_CORES;
import static org.infinitest.eclipse.prefs.PreferencesConstants.SLOW_TEST_WARNING;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.infinitest.MultiCoreConcurrencyController;
import org.infinitest.eclipse.InfinitestPlugin;
import org.infinitest.eclipse.markers.ProblemMarkerRegistry;
import org.infinitest.eclipse.markers.SlowMarkerRegistry;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private final PreferenceChangeHandler handler;

	public PreferencePage() {
		this(InfinitestPlugin.getInstance().getBean(PreferenceChangeHandler.class),
				InfinitestPlugin.getInstance().getBean(ProblemMarkerRegistry.class),
				InfinitestPlugin.getInstance().getBean(SlowMarkerRegistry.class));
	}

	PreferencePage(PreferenceChangeHandler changeHandler, ProblemMarkerRegistry problemMarkerRegistry, SlowMarkerRegistry slowMarkerRegistry) {
		handler = changeHandler;
		handler.setProblemMarkerRegistry(problemMarkerRegistry);
		handler.setSlowMarkerRegistry(slowMarkerRegistry);
	}

	@Override
	public void init(IWorkbench workbench) {
		initializePreferenceStoreToDefaultLocation();
	}

	private void initializePreferenceStoreToDefaultLocation() {
		IPreferenceStore preferenceStore = InfinitestPlugin.getInstance().getPreferenceStore();
		setPreferenceStore(preferenceStore);
	}

	@Override
	protected void createFieldEditors() {
		addField(createAutoTestEditor());
		addField(createParallelizationEditor());
		addField(createSlowTestWarningCutoffEditor());
		addField(createSeverityEditor(PreferencesConstants.FAILED_TEST_MARKER_SEVERITY, "Failed test severity"));
		addField(createSeverityEditor(PreferencesConstants.SLOW_TEST_MARKER_SEVERITY, "Slow test severity"));
		addField(createFailingBackgroundColorEditor());
		addField(createFailingTextColorEditor());
	}

	private BooleanFieldEditor createAutoTestEditor() {
		return new BooleanFieldEditor(AUTO_TEST, "Continuously Test", getFieldEditorParent());
	}

	private SwtColorFieldEditor createFailingBackgroundColorEditor() {
		return new SwtColorFieldEditor(PreferencesConstants.FAILING_BACKGROUND_COLOR, "Fail Background Color", getFieldEditorParent());
	}

	private SwtColorFieldEditor createFailingTextColorEditor() {
		return new SwtColorFieldEditor(PreferencesConstants.FAILING_TEXT_COLOR, "Fail Text Color", getFieldEditorParent());
	}

	private FieldEditor createSlowTestWarningCutoffEditor() {
		IntegerFieldEditor editor = new IntegerFieldEditor(SLOW_TEST_WARNING, "Slow Test Warning (milliseconds)", getFieldEditorParent());
		editor.setEmptyStringAllowed(false);
		editor.setValidRange(1, MAX_VALUE);
		return editor;
	}

	private FieldEditor createParallelizationEditor() {
		IntegerFieldEditor editor = new IntegerFieldEditor(PARALLEL_CORES, "Projects Tested Concurrently", getFieldEditorParent());
		editor.setEmptyStringAllowed(false);
		editor.setValidRange(1, MultiCoreConcurrencyController.DEFAULT_MAX_CORES);
		return editor;
	}

	private ComboFieldEditor createSeverityEditor(String name, String labelText) {
		String[][] values = new String[][] {
		    {"Info", String.valueOf(IMarker.SEVERITY_INFO)},
		    {"Warning", String.valueOf(IMarker.SEVERITY_WARNING)},
		    {"Error", String.valueOf(IMarker.SEVERITY_ERROR)}};
		    
		return new ComboFieldEditor(name, labelText, values, getFieldEditorParent());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		handler.propertyChange(event);
	}

	@Override
	public void performApply() {
		handler.applyChanges();
		super.performApply();
	}

	@Override
	public boolean performOk() {
		handler.applyChanges();
		return super.performOk();
	}

	@Override
	public boolean performCancel() {
		handler.clearChanges();
		return super.performCancel();
	}

	@Override
	public void performDefaults() {
		handler.clearSlowMarkers();
		super.performDefaults();
	}
}
