package org.infinitest.testrunner;

import static java.lang.System.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.EventSupport.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WhenCalculatingTestStatistics
{
    private RunStatistics statistics;

    @Before
    public void inContext()
    {
        statistics = new RunStatistics();
    }

    @Test
    public void shouldProvideLastFailureTime()
    {
        statistics.testCaseComplete(testCaseFailing("test1", "", new Throwable()));
        assertThat(currentTimeMillis() - statistics.getLastFailureTime("test1"), lessThan(10l));
    }

    @Test
    public void shouldReturnZeroForTestsThatHaveNeverFailed()
    {
        assertEquals(0, statistics.getLastFailureTime("UnknownTest"));
    }

    @Test
    public void shouldTreatErrorsLikeFailures()
    {
        statistics.testCaseComplete(testCaseFailing("test1", "", new Exception()));
        assertThat(currentTimeMillis() - statistics.getLastFailureTime("test1"), lessThan(10l));
    }
}
