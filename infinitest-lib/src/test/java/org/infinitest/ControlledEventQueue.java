package org.infinitest;

import static com.google.common.collect.Lists.*;

import java.util.ArrayList;
import java.util.List;

public class ControlledEventQueue implements EventQueue
{
    private List<Runnable> events = newArrayList();

    public synchronized void push(Runnable runnable)
    {
        events.add(runnable);
    }

    public synchronized void flush()
    {
        // We make a copy because some event handlers fire new events
        ArrayList<Runnable> eventsToFire = newArrayList(events);
        events.clear();
        for (Runnable event : eventsToFire)
            event.run();
        if (!events.isEmpty())
            flush();
    }

    public void pushNamed(NamedRunnable runnable)
    {
        push(runnable);
    }
}
