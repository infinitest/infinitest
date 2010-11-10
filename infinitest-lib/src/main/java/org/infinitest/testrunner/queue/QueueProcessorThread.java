package org.infinitest.testrunner.queue;

class QueueProcessorThread extends Thread
{
    private final ProcessorRunnable runnable;

    public QueueProcessorThread(ProcessorRunnable runnable)
    {
        super(runnable);
        this.runnable = runnable;
    }

    public void terminate()
    {
        runnable.terminate();
    }
}