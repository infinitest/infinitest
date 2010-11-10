package org.infinitest.eclipse;

import static org.eclipse.core.runtime.jobs.Job.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.infinitest.EventQueue;
import org.infinitest.NamedRunnable;
import org.infinitest.QueueDispatchException;
import org.springframework.stereotype.Component;

@Component
public class SwtEventQueue implements EventQueue
{
    private BlockingQueue<NamedRunnable> runnableQueue = new LinkedBlockingQueue<NamedRunnable>();
    private Job job;

    public SwtEventQueue()
    {
        job = createJob();
    }

    public void pushNamed(NamedRunnable runnable)
    {
        try
        {
            runnableQueue.put(runnable);
        }
        catch (InterruptedException e)
        {
            throw new QueueDispatchException(e);
        }
        // Job API will queue up a successive job if it is already running, but not if there is
        // already one queued
        job.schedule();
    }

    private class InfinitestJob extends Job
    {
        public InfinitestJob()
        {
            super("Infinitest");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
            monitor.beginTask("Processing events", runnableQueue.size());
            try
            {
                int eventsProcessed = 0;
                while (!runnableQueue.isEmpty() && !monitor.isCanceled())
                {
                    processEvent(monitor);
                    monitor.worked(eventsProcessed++);
                }
            }
            catch (InterruptedException e)
            {
                return Status.CANCEL_STATUS;
            }
            finally
            {
                monitor.done();
            }
            return Status.OK_STATUS;
        }

        private void processEvent(IProgressMonitor monitor) throws InterruptedException
        {
            NamedRunnable runnable = runnableQueue.take();
            setName("Infinitest [" + runnable.getName() + "]");
            try
            {
                runnable.run();
            }
            // CHECKSTYLE:OFF
            // We don't want a failure in the processing of one event to prevent the next.
            catch (Exception e)
            // CHECKSTYLE:ON
            {
                log("Error executing event runnable: " + runnable.getName(), e);
            }
        }
    }

    Job createJob()
    {
        job = new InfinitestJob();
        job.setPriority(BUILD);
        return job;
    }

    public void push(final Runnable runnable)
    {
        this.pushNamed(new NamedRunnable("")
        {
            public void run()
            {
                runnable.run();
            }
        });
    }
}
