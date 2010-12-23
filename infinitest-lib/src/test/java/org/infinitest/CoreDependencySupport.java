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

import static org.infinitest.testrunner.TestRunnerMother.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assume.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.infinitest.changedetect.ChangeDetector;
import org.infinitest.changedetect.FakeChangeDetector;
import org.infinitest.parser.FakeJavaClass;
import org.infinitest.parser.JavaClass;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.InProcessRunner;
import org.infinitest.util.InfinitestTestUtils;
import org.junit.Test;

import com.fakeco.fakeproduct.TestFakeProduct;
import com.fakeco.fakeproduct.simple.FailingTest;
import com.fakeco.fakeproduct.simple.PassingTest;

public class CoreDependencySupport
{
    public static final Class<?> FAILING_TEST = FailingTest.class;
    public static final Class<?> PASSING_TEST = PassingTest.class;
    public static final Class<?> SLOW_TEST = SlowTest.class;

    public static class SlowTest
    {
        @Test
        public void shouldBeReallySlow() throws Exception
        {
            assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
            Thread.sleep(1000000);
        }
    }

    private CoreDependencySupport()
    {
        // nothing to do here
    }

    public static TestDetector withTests(final Class<?>... testClasses)
    {
        return new StubTestDetector()
        {
            @Override
            public Set<JavaClass> findTestsToRun(Collection<File> changedFiles)
            {
                Set<JavaClass> testsToRun = new HashSet<JavaClass>();
                if (!isCleared())
                {
                    for (Class<?> each : testClasses)
                    {
                        testsToRun.add(new FakeJavaClass(each.getName()));
                    }
                }

                return testsToRun;
            }

            @Override
            public boolean isEmpty()
            {
                return false;
            }
        };
    }

    public static TestDetector withNoTestsToRun()
    {
        return mock(TestDetector.class);
    }

    public static ChangeDetector withChangedFiles(Class<?>... changedClasses)
    {
        if (changedClasses.length == 0)
        {
            createChangeDetector(new Class<?>[] { TestFakeProduct.class });
        }
        return createChangeDetector(changedClasses);
    }

    private static ChangeDetector createChangeDetector(Class<?>... changedClasses)
    {
        Set<File> changedFiles = new HashSet<File>();
        for (Class<?> each : changedClasses)
        {
            changedFiles.add(getFileForClass(each));
        }
        return new FakeChangeDetector(changedFiles, false);
    }

    public static ChangeDetector withNoChangedFiles()
    {
        return createChangeDetector();
    }

    static DefaultInfinitestCore createCore(ChangeDetector changedFiles, TestDetector tests)
    {
        return createCore(changedFiles, tests, new FakeEventQueue());
    }

    static DefaultInfinitestCore createCore(ChangeDetector changedFiles, TestDetector tests, EventQueue eventQueue)
    {
        DefaultInfinitestCore core = new DefaultInfinitestCore(new InProcessRunner(), eventQueue);
        core.setChangeDetector(changedFiles);
        core.setTestDetector(tests);
        return core;
    }

    static DefaultInfinitestCore createAsyncCore(ChangeDetector changeDetector, TestDetector testDetector)
    {
        DefaultInfinitestCore core = new DefaultInfinitestCore(createRunner(), new FakeEventQueue());
        core.setChangeDetector(changeDetector);
        core.setTestDetector(testDetector);
        return core;
    }
}
