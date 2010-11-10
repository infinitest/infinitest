package org.infinitest.intellij;

import static org.hamcrest.Matchers.*;
import static org.infinitest.util.CollectionUtils.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;

import org.infinitest.intellij.idea.language.IdeaInfinitestAnnotator;
import org.junit.Before;
import org.junit.Test;

public class WhenAnnotatingClass
{
    private IdeaInfinitestAnnotator annotator;

    @Before
    public void setUp()
    {
        annotator = IdeaInfinitestAnnotator.getInstance();
    }

    @Test
    public void shouldAnnotateTopLevelClass()
    {
        annotator.annotate(eventWithError(new Exception()));
        assertThat(first(annotator.getTestEvents()).getPointOfFailureClassName(), is(getClass().getName()));
    }

    @Test
    public void shouldAnnotateContainingClassOfInnerClassFailures()
    {
        annotator.annotate(eventWithError(InnerClass.createException()));
        assertThat(first(annotator.getTestEvents()).getPointOfFailureClassName(), is(getClass().getName()));
    }

    static class InnerClass
    {
        public static Throwable createException()
        {
            try
            {
                fail("Intentional exception");
            }
            catch (Throwable e)
            {
                return e;
            }

            return null;
        }
    }
}
