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

import static java.util.Arrays.*;

import java.util.List;

import org.infinitest.NamedRunnable;
import org.infinitest.eclipse.markers.MarkerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MarkerClearingRunnable extends NamedRunnable
{
    private final List<MarkerRegistry> registries;

    @Autowired
    public MarkerClearingRunnable(MarkerRegistry... markerRegistries)
    {
        super("Clearing markers");
        registries = asList(markerRegistries);
        if (registries.isEmpty())
        {
            throw new IllegalArgumentException("No marker registries to clear! Expected at least two." + registries);
        }
    }

    public void run()
    {
        for (MarkerRegistry each : registries)
        {
            each.clear();
        }
    }
}