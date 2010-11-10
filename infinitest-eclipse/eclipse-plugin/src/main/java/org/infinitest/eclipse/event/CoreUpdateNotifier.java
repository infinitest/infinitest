package org.infinitest.eclipse.event;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.*;

import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.infinitest.EventQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class CoreUpdateNotifier implements IResourceChangeListener
{
    private final List<EclipseEventProcessor> processors;
    private final EventQueue queue;

    @Autowired
    CoreUpdateNotifier(EventQueue queue)
    {
        this.queue = queue;
        processors = newArrayList();
    }

    @Autowired
    public void addProcessor(EclipseEventProcessor... eventProcessors)
    {
        processors.addAll(asList(eventProcessors));
    }

    /**
     * http://www.eclipse.org/articles/Article-Resource-deltas/resource-deltas.html
     */
    public void resourceChanged(final IResourceChangeEvent event)
    {
        if (event.getDelta() != null)
        {
            processEvent(event);
        }
    }

    public void processEvent(IResourceChangeEvent event)
    {
        for (EclipseEventProcessor processor : processors)
        {
            if (processor.canProcessEvent(event))
            {
                queue.pushNamed(new EventProcessorRunnable(processor, event));
            }
        }
    }
}
