package org.infinitest;

import static java.util.Collections.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.testrunner.TestRunnerMother.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assume.*;

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
import org.jmock.Expectations;
import org.jmock.Mockery;
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
                        testsToRun.add(new FakeJavaClass(each.getName()));
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

    @SuppressWarnings("unchecked")
    public static TestDetector withNoTestsToRun()
    {
        Mockery mockery = new Mockery();
        final TestDetector graph = mockery.mock(TestDetector.class);
        mockery.checking(new Expectations()
        {
            {
                allowing(graph).setClasspathProvider(with(a(RuntimeEnvironment.class)));
                allowing(graph).setClasspathProvider((ClasspathProvider) with(nullValue()));
                allowing(graph).clear();
                allowing(graph).getCurrentTests();
                will(returnValue(emptySet()));
                allowing(graph).findTestsToRun(with(any(Set.class)));
                will(returnValue(emptySet()));
                allowing(graph).isEmpty();
                will(returnValue(true));
            }
        });
        return graph;
    }

    public static ChangeDetector withChangedFiles(Class<?>... changedClasses)
    {
        if (changedClasses.length == 0)
            createChangeDetector(new Class<?>[] { TestFakeProduct.class });
        return createChangeDetector(changedClasses);
    }

    private static ChangeDetector createChangeDetector(Class<?>... changedClasses)
    {
        Set<File> changedFiles = new HashSet<File>();
        for (Class<?> each : changedClasses)
            changedFiles.add(getFileForClass(each));
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
