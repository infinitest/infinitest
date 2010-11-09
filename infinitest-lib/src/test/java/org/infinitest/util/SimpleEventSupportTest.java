package org.infinitest.util;

import static org.infinitest.util.Events.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

// Reflection is OK. I promise.
// http://www.jguru.com/faq/view.jsp?EID=246569
public class SimpleEventSupportTest
{
    protected boolean eventRecieved;
    private Events<SimpleListener> integerEvent;
    private SimpleListener listener;

    @Before
    public void inContext()
    {
        integerEvent = eventFor(SimpleListener.class);
        listener = new SimpleListener()
        {
            public void eventFired()
            {
                eventRecieved = true;
            }
        };
        integerEvent.addListener(listener);
    }

    @Test
    public void shouldFireEventsWithNoParameter()
    {
        integerEvent.fire();
        assertTrue(eventRecieved);
    }

    @Test
    public void canRemoveListeners()
    {
        integerEvent.removeListener(listener);
        integerEvent.fire();
        assertFalse(eventRecieved);
    }

    public static interface SimpleListener
    {
        public void eventFired();
    }
}
