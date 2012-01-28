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
package org.infinitest.intellij;

import static java.lang.Boolean.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.infinitest.intellij.idea.facet.*;
import org.jdom.*;
import org.junit.*;

import com.intellij.openapi.util.*;

public class WhenWritingConfigurationForTheFirstTime {
	private InfinitestFacetConfiguration configuration;
	private Element element;

	@Before
	public void setUp() {
		configuration = new InfinitestFacetConfiguration();
		element = new Element("config");
	}

	@Test
	public void shouldStoreScmUpdateEnabled() throws WriteExternalException {
		configuration.setScmUpdateEnabled(true);
		configuration.writeExternal(element);

		assertThat(element.getAttribute("scmUpdateGreenHook").getValue(), is(TRUE.toString()));
	}

	@Test
	public void shouldStoreScmUpdateDisabled() throws WriteExternalException {
		configuration.setScmUpdateEnabled(false);
		configuration.writeExternal(element);

		assertThat(element.getAttribute("scmUpdateGreenHook").getValue(), is(FALSE.toString()));
	}
}
