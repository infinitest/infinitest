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

import static org.junit.Assert.*;

import org.eclipse.core.resources.IResourceChangeListener;
import org.infinitest.eclipse.workspace.ResourceFinder;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PluginContextIntegrationTest
{
    @Test
    public void shouldWireComponentsTogetherByTypeUsingSpringAutowiring()
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
                        "/META-INF//spring/plugin-context.xml", "/META-INF/spring/eclipse-test-context.xml" });
        // The main point of contact from Eclipse to Infinitest is this resource change listener, so
        // we check for that.
        assertFalse(context.getBeansOfType(IResourceChangeListener.class).isEmpty());

        assertFalse(context.getBeansOfType(ResourceFinder.class).isEmpty());
    }
}
