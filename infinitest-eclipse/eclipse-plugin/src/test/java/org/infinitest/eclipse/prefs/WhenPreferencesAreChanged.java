package org.infinitest.eclipse.prefs;

import static org.easymock.EasyMock.*;
import static org.eclipse.jface.preference.FieldEditor.*;
import static org.infinitest.eclipse.prefs.PreferencesConstants.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;
import static org.junit.Assert.*;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.infinitest.eclipse.PluginActivationController;
import org.infinitest.eclipse.workspace.CoreSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class WhenPreferencesAreChanged
{
    private FieldEditor eventSource;
    private String preferenceName;
    private PluginActivationController controller;
    private PreferenceChangeHandler handler;
    private CoreSettings coreSettings;

    @Before
    public void setUp()
    {
        controller = createMock(PluginActivationController.class);
        coreSettings = Mockito.mock(CoreSettings.class);
        handler = new PreferenceChangeHandler(controller, coreSettings);
        preferenceName = AUTO_TEST;
        eventSource = new BooleanFieldEditor()
        {
            @Override
            public String getPreferenceName()
            {
                return preferenceName;
            }
        };
    }

    @After
    public void cleanup()
    {
        resetToDefaults();
    }

    @Test
    public void shouldStartContinouslyTestingIfSelectionIsChecked()
    {
        controller.enable();
        checkExpectations(AUTO_TEST, false, true);
    }

    @Test
    public void shouldStopContinouslyTestingIfSelectionIsChecked()
    {
        controller.disable();
        checkExpectations(AUTO_TEST, true, false);
    }

    @Test
    public void shouldIgnoreIfPropertyNameDoesNotMatch()
    {
        preferenceName = "SomeEventName";
        checkExpectations(preferenceName, true, false);
    }

    @Test
    public void shouldPromptForLicenseIfPluginIsEnabledWithInvalidKey()
    {
        controller.enable();
        // Show license dialog now
        checkExpectations(AUTO_TEST, false, true);
    }

    @Test
    public void shouldAdjustWarningTimeout()
    {
        preferenceName = SLOW_TEST_WARNING;
        checkExpectations(SLOW_TEST_WARNING, "500", "100");
        assertEquals(100, getSlowTestTimeLimit());
    }

    @Test
    public void shouldAdjustSemaphorePermits()
    {
        preferenceName = PARALLEL_CORES;
        checkExpectations(PARALLEL_CORES, "1", "2");
        Mockito.verify(coreSettings).setConcurrentCoreCount(2);
    }

    @Test
    public void shouldIgnoreOtherPropertyTypes()
    {
        replay(controller);
        preferenceName = PARALLEL_CORES;
        handler.propertyChange(new PropertyChangeEvent(eventSource, IS_VALID, false, true));
        verify(controller);
    }

    @Test
    public void shouldIgnoreBlankValues()
    {
        preferenceName = PARALLEL_CORES;
        checkExpectations(PARALLEL_CORES, "", "");
    }

    private void checkExpectations(String propertyName, Object previousState, Object newState)
    {
        replay(controller);
        handler.propertyChange(new PropertyChangeEvent(eventSource, VALUE, previousState, newState));
        verify(controller);
    }

}
