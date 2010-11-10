package org.infinitest.eclipse.trim;

import org.infinitest.StatusChangeListener;
import org.infinitest.TestQueueListener;
import org.infinitest.eclipse.AggregateResultsListener;
import org.infinitest.eclipse.status.WorkspaceStatusListener;

//DEBT Break this apart into presenter (monitors status) and controller (updates workspace status based on cores)
public interface VisualStatusRegistry extends TestQueueListener, SaveListener, AggregateResultsListener,
                WorkspaceStatusListener, StatusChangeListener
{
    void updateVisualStatus(VisualStatus status);
}