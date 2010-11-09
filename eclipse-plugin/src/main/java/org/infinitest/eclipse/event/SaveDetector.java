package org.infinitest.eclipse.event;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.trim.SaveListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class SaveDetector extends EclipseEventProcessor
{
    private final SaveListener saveListener;

    @Autowired
    SaveDetector(SaveListener saveListener)
    {
        super("Looking for changes");
        this.saveListener = saveListener;
    }

    @Override
    public boolean canProcessEvent(IResourceChangeEvent event)
    {
        DeltaVisitor visitor = new DeltaVisitor();
        try
        {
            event.getDelta().accept(visitor, true);
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }
        return visitor.savedResourceFound();
    }

    @Override
    public void processEvent(IResourceChangeEvent event) throws CoreException
    {
        saveListener.filesSaved();
    }
}
