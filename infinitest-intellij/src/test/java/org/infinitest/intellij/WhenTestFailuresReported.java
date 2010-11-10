package org.infinitest.intellij;

import static java.util.Collections.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.mockito.Mockito.*;

import java.util.List;

import junit.framework.AssertionFailedError;

import org.infinitest.FakeInfinitestCore;
import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.intellij.plugin.launcher.InfinitestPresenter;
import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class WhenTestFailuresReported
{
    private InfinitestAnnotator annotator;
    private InfinitestPresenter presenter;
    private final TestEvent failure = methodFailed("message", "test", "method", new AssertionFailedError());
    private static final List<TestEvent> EMPTY_LIST = emptyList();

    @Before
    public void setUp()
    {
        InfinitestCore core = new FakeInfinitestCore();
        annotator = mock(InfinitestAnnotator.class);
        presenter = new InfinitestPresenter(new ResultCollector(core), core, new FakeInfinitestView(),
                        new FakeTestControl(), annotator);
    }

    @Test
    public void shouldAnnotateFailuresAdded()
    {
        presenter.failureListChanged(singletonList(failure), EMPTY_LIST);

        verify(annotator).annotate(failure);
    }

    @Test
    public void shouldClearFailuresRemoved()
    {
        presenter.failureListChanged(EMPTY_LIST, singletonList(failure));

        verify(annotator).clearAnnotation(failure);
    }
}
