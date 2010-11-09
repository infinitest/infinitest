package org.infinitest.intellij;

import static org.hamcrest.Matchers.is;
import org.infinitest.intellij.idea.language.IdeaInfinitestAnnotator;
import static org.infinitest.util.CollectionUtils.first;
import static org.infinitest.util.EventFakeSupport.eventWithError;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.Before;

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
