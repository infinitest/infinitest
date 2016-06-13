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
package org.infinitest.eclipse;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Arrays.asList;
import static org.infinitest.eclipse.prefs.PreferencesConstants.PARALLEL_CORES;
import static org.infinitest.eclipse.prefs.PreferencesConstants.SLOW_TEST_WARNING;
import static org.infinitest.util.InfinitestGlobalSettings.getSlowTestTimeLimit;
import static org.infinitest.util.InfinitestUtils.addLoggingListener;

import java.util.logging.Level;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.infinitest.eclipse.workspace.CoreSettings;
import org.infinitest.util.InfinitestGlobalSettings;
import org.infinitest.util.InfinitestUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.annotations.VisibleForTesting;

/**
 * Controls the plug-in life cycle.
 */
public class InfinitestPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.infinitest.eclipse";
	private static InfinitestPlugin sharedInstance;
	private static Bundle pluginBundle;

	private ApplicationContext context;

	static {
		addLoggingListener(new EclipseLoggingListener());
	}

	@Override
	// CHECKSTYLE:OFF
	// Idiomatic OSGI and checkstyle don't like each other
	public void start(BundleContext context) throws Exception
	// CHECKSTYLE:ON
	{
		super.start(context);
		sharedInstance = this;
	}

	@Override
	// CHECKSTYLE:OFF
	// Idiomatic OSGI and checkstyle don't like each other
	public void stop(BundleContext context) throws Exception
	// CHECKSTYLE:ON
	{
		sharedInstance = null;
		super.stop(context);
	}

	// Idiomatic OSGI
	public static InfinitestPlugin getInstance() {
		return sharedInstance;
	}

	public void startContinuouslyTesting() {
		getPluginController().enable();
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(PARALLEL_CORES, 1);
		store.setDefault(SLOW_TEST_WARNING, getSlowTestTimeLimit());
	}

	// Only used for testing.
	public void setPluginBundle(Bundle bundle) {
		pluginBundle = bundle;
	}

	public Bundle getPluginBundle() {
		if ((pluginBundle == null) && (InfinitestPlugin.getInstance() != null)) {
			return InfinitestPlugin.getInstance().getBundle();
		}
		return pluginBundle;
	}

	public PluginActivationController getPluginController() {
		return getBean(PluginActivationController.class);
	}

	public <T> T getBean(Class<T> beanClass) {
		if (context == null) {
      context = new AnnotationConfigApplicationContext(InfinitestConfig.class);

			restoreSavedPreferences(getPluginPreferences(), getBean(CoreSettings.class));
			InfinitestUtils.log(Level.FINE, "Beans loaded: " + asList(context.getBeanDefinitionNames()));
		}
		return getOnlyElement(context.getBeansOfType(beanClass).values());
	}

	@VisibleForTesting
	void restoreSavedPreferences(Preferences preferences, CoreSettings coreSettings) {
		coreSettings.setConcurrentCoreCount(preferences.getInt(PARALLEL_CORES));
		InfinitestGlobalSettings.setSlowTestTimeLimit(preferences.getLong(SLOW_TEST_WARNING));
	}
}
