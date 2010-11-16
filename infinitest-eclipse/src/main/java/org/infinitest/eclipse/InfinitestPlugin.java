/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.eclipse;

import static com.google.common.collect.Iterables.*;
import static java.util.Arrays.*;
import static org.infinitest.eclipse.prefs.PreferencesConstants.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;
import static org.infinitest.util.InfinitestUtils.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.infinitest.eclipse.workspace.CoreSettings;
import org.infinitest.util.InfinitestUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Controls the plug-in life cycle.
 */
public class InfinitestPlugin extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.infinitest.eclipse";
    private static InfinitestPlugin sharedInstance;
    private static Bundle pluginBundle;

    private ClassPathXmlApplicationContext context;

    static
    {
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
    public static InfinitestPlugin getInstance()
    {
        return sharedInstance;
    }

    public void startContinuouslyTesting()
    {
        getPluginController().enable();
    }

    @Override
    protected void initializeDefaultPreferences(IPreferenceStore store)
    {
        store.setDefault(PARALLEL_CORES, 1);
        store.setDefault(SLOW_TEST_WARNING, getSlowTestTimeLimit());
    }

    // Only used for testing.
    public void setPluginBundle(Bundle bundle)
    {
        pluginBundle = bundle;
    }

    public Bundle getPluginBundle()
    {
        if ((pluginBundle == null) && (InfinitestPlugin.getInstance() != null))
        {
            return InfinitestPlugin.getInstance().getBundle();
        }
        return pluginBundle;
    }

    public PluginActivationController getPluginController()
    {
        return getBean(PluginActivationController.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> beanClass)
    {
        if (context == null)
        {
            context = new ClassPathXmlApplicationContext(new String[] { "/META-INF/spring/plugin-context.xml",
                            "/META-INF/spring/eclipse-context.xml" });

            getBean(CoreSettings.class).setConcurrentCoreCount(getPluginPreferences().getInt(PARALLEL_CORES));
            InfinitestUtils.log("Beans loaded: " + asList(context.getBeanDefinitionNames()));
        }
        return (T) getOnlyElement(context.getBeansOfType(beanClass).values());
    }
}
