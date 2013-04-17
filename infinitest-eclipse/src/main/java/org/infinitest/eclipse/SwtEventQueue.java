/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.eclipse;

import static org.eclipse.core.runtime.jobs.Job.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.util.concurrent.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.infinitest.*;
import org.springframework.stereotype.*;

@Component
public class SwtEventQueue implements EventQueue {
	private final BlockingQueue<NamedRunnable> runnableQueue = new LinkedBlockingQueue<NamedRunnable>();
	private Job job;

	public SwtEventQueue() {
		job = createJob();
	}

	@Override
	public void pushNamed(NamedRunnable runnable) {
		try {
			runnableQueue.put(runnable);
		} catch (InterruptedException e) {
			throw new QueueDispatchException(e);
		}
		// Job API will queue up a successive job if it is already running, but
		// not if there is
		// already one queued
		job.schedule();
	}

	private class InfinitestJob extends Job {
		public InfinitestJob() {
			super("Infinitest");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("Processing events", runnableQueue.size());
			try {
				int eventsProcessed = 0;
				while (!runnableQueue.isEmpty() && !monitor.isCanceled()) {
					processEvent(monitor);
					monitor.worked(eventsProcessed++);
				}
			} catch (InterruptedException e) {
				return Status.CANCEL_STATUS;
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}

		private void processEvent(IProgressMonitor monitor) throws InterruptedException {
			NamedRunnable runnable = runnableQueue.take();
			setName("Infinitest [" + runnable.getName() + "]");
			try {
				runnable.run();
			}
			// CHECKSTYLE:OFF
			// We don't want a failure in the processing of one event to prevent
			// the next.
			catch (Exception e)
			// CHECKSTYLE:ON
			{
				log("Error executing event runnable: " + runnable.getName(), e);
			}
		}
	}

	Job createJob() {
		job = new InfinitestJob();
		job.setPriority(BUILD);
		return job;
	}

	@Override
	public void push(final Runnable runnable) {
		pushNamed(new NamedRunnable("") {
			@Override
			public void run() {
				runnable.run();
			}
		});
	}
}
