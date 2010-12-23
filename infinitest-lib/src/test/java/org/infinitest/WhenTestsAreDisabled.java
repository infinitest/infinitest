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
package org.infinitest;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Sets.*;
import static org.infinitest.CoreDependencySupport.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.infinitest.parser.JavaClass;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.TestRunner;
import org.junit.Test;

import com.fakeco.fakeproduct.simple.PassingTest;

public class WhenTestsAreDisabled
{
    @SuppressWarnings("unchecked")
    @Test
    public void shouldFireAppropriateEvent()
    {
        TestRunner runner = mock(TestRunner.class);
        TestDetector testDetector = mock(TestDetector.class);
        when(testDetector.getCurrentTests()).thenReturn(setify("MyClass", "OtherClass"), setify("OtherClass"));
        Set<JavaClass> emptyClassSet = Collections.<JavaClass> emptySet();
        when(testDetector.findTestsToRun(any(Collection.class))).thenReturn(emptyClassSet);

        DefaultInfinitestCore core = new DefaultInfinitestCore(runner, new ControlledEventQueue());
        core.setChangeDetector(withChangedFiles(PassingTest.class));
        core.setTestDetector(testDetector);
        final Set<String> disabledTestList = newHashSet();
        core.addDisabledTestListener(new DisabledTestListener()
        {
            public void testsDisabled(Collection<String> testName)
            {
                disabledTestList.addAll(testName);
            }
        });
        core.update();
        assertEquals("MyClass", getOnlyElement(disabledTestList));
    }
}
