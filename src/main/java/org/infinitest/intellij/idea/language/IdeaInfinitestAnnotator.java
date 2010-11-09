package org.infinitest.intellij.idea.language;

import static java.util.Collections.*;

import java.util.HashSet;
import java.util.Set;

import org.infinitest.intellij.InfinitestAnnotator;
import org.infinitest.testrunner.TestEvent;

public class IdeaInfinitestAnnotator implements InfinitestAnnotator
{
    private Set<InnerClassFriendlyTestEvent> events = new HashSet<InnerClassFriendlyTestEvent>();

    private static IdeaInfinitestAnnotator instance;

    private IdeaInfinitestAnnotator()
    {

    }

    public static IdeaInfinitestAnnotator getInstance()
    {
        if (instance == null)
        {
            instance = new IdeaInfinitestAnnotator();
        }
        return instance;
    }

    public void annotate(final TestEvent event)
    {
        events.add(new InnerClassFriendlyTestEvent(event));
    }

    public void clearAnnotation(TestEvent event)
    {
        events.remove(new InnerClassFriendlyTestEvent(event));
    }

    public Set<InnerClassFriendlyTestEvent> getTestEvents()
    {
        return unmodifiableSet(events);
    }
}
