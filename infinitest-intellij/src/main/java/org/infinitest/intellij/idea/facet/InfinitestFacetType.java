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
package org.infinitest.intellij.idea.facet;

import javax.swing.*;

import org.jetbrains.annotations.*;

import com.intellij.facet.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.util.*;

public class InfinitestFacetType extends FacetType<InfinitestFacet, InfinitestFacetConfiguration> {
	public static final InfinitestFacetType INSTANCE = new InfinitestFacetType();

	private InfinitestFacetType() {
		super(InfinitestFacet.ID, "Infinitest", "Infinitest");
	}

	@Override
	public InfinitestFacetConfiguration createDefaultConfiguration() {
		return new InfinitestFacetConfiguration();
	}

	@Override
	public InfinitestFacet createFacet(@NotNull Module module, String name, @NotNull InfinitestFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
		configuration.setModule(module);
		return new InfinitestFacet(this, module, name, configuration, underlyingFacet);
	}

	@Override
	public Icon getIcon() {
		return IconLoader.getIcon("/infinitest.png");
	}

	@Override
	public boolean isSuitableModuleType(ModuleType moduleType) {
		return StdModuleTypes.JAVA.equals(moduleType) || "PLUGIN_MODULE".equals(moduleType.getId());
	}
}
