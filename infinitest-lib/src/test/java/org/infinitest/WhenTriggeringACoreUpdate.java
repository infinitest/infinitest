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

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.infinitest.parser.FakeJavaClass;
import org.infinitest.parser.JavaClass;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.TestRunner;
import org.junit.Before;
import org.junit.Test;

public class WhenTriggeringACoreUpdate
{
    private List<File> updatedFiles;
    private DefaultInfinitestCore core;
    private TestDetector testDetector;

    @Before
    public void inContext()
    {
        updatedFiles = newArrayList();
        core = new DefaultInfinitestCore(createMock(TestRunner.class), new ControlledEventQueue());
        testDetector = createMock(TestDetector.class);
        expect(testDetector.getCurrentTests()).andReturn(Collections.<String> emptySet()).times(2);
        core.setTestDetector(testDetector);
    }

    private void testsToExpect(JavaClass... tests)
    {
        expect(testDetector.findTestsToRun(updatedFiles)).andReturn(newHashSet(tests));
    }

    @Test
    public void canUseAKnownListOfChangedFilesToReduceFileSystemAccess()
    {
        testsToExpect();
        replay(testDetector);
        core.update(updatedFiles);
        verify(testDetector);
    }

    @Test
    public void shouldReturnTheNumberOfTestsRun()
    {
        testsToExpect(new FakeJavaClass("FakeTest"));
        replay(testDetector);

        assertEquals(1, core.update(updatedFiles));
    }
}
