package org.infinitest.eclipse.prefs;

import static java.lang.Integer.*;
import static org.infinitest.eclipse.prefs.PreferencesConstants.*;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.infinitest.eclipse.InfinitestPlugin;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    private final PreferenceChangeHandler handler;

    public PreferencePage()
    {
        this(InfinitestPlugin.getInstance().getBean(PreferenceChangeHandler.class));
    }

    PreferencePage(PreferenceChangeHandler changeHandler)
    {
        handler = changeHandler;
    }

    public void init(IWorkbench workbench)
    {
        initializePreferenceStoreToDefaultLocation();
    }

    private void initializePreferenceStoreToDefaultLocation()
    {
        IPreferenceStore preferenceStore = InfinitestPlugin.getInstance().getPreferenceStore();
        setPreferenceStore(preferenceStore);
    }

    @Override
    protected void createFieldEditors()
    {
        BooleanFieldEditor autoTestEditor = new BooleanFieldEditor(AUTO_TEST, "Continuously Test",
                        getFieldEditorParent());
        addField(autoTestEditor);
        addField(createParallelizationEditor());
        addField(createSlowTestWarningCutoffEditor());
    }

    private FieldEditor createSlowTestWarningCutoffEditor()
    {
        IntegerFieldEditor editor = new IntegerFieldEditor(SLOW_TEST_WARNING, "Slow Test Warning (milliseconds)",
                        getFieldEditorParent());
        editor.setEmptyStringAllowed(false);
        editor.setValidRange(1, MAX_VALUE);
        return editor;
    }

    private FieldEditor createParallelizationEditor()
    {
        IntegerFieldEditor editor = new IntegerFieldEditor(PARALLEL_CORES, "Projects Tested Concurrently",
                        getFieldEditorParent());
        editor.setEmptyStringAllowed(false);
        editor.setValidRange(1, 16);
        return editor;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        handler.propertyChange(event);
    }
}
