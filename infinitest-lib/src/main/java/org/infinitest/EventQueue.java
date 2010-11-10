package org.infinitest;

public interface EventQueue
{
    /**
     * Pushes a new runnable on to the UI's event queue, to be executed asynchronously at the next
     * available opportunity.
     */
    void push(Runnable runnable);

    void pushNamed(NamedRunnable runnable);
}
