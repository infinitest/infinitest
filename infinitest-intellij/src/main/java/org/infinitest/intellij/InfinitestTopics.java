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
package org.infinitest.intellij;

import org.infinitest.ConsoleOutputListener;
import org.infinitest.DisabledTestListener;
import org.infinitest.FailureListListener;
import org.infinitest.StatusChangeListener;
import org.infinitest.TestQueueListener;
import org.infinitest.testrunner.TestResultsListener;

import com.intellij.util.messages.Topic;

public interface InfinitestTopics {

	public static final Topic<ConsoleOutputListener> CONSOLE_TOPIC = Topic.create("Infinitest console output", ConsoleOutputListener.class);
	public static final Topic<DisabledTestListener> DISABLED_TEST_TOPIC = Topic.create("Infinitest disabled tests", DisabledTestListener.class);
	public static final Topic<FailureListListener> FAILURE_LIST_TOPIC = Topic.create("Infinitest disabled tests", FailureListListener.class);
	public static final Topic<StatusChangeListener> STATUS_CHANGE_TOPIC = Topic.create("Infinitest core status", StatusChangeListener.class);
	public static final Topic<TestQueueListener> TEST_QUEUE_TOPIC  = Topic.create("Infinitest test queue", TestQueueListener.class);
	public static final Topic<TestResultsListener> TEST_RESULTS_TOPIC = Topic.create("Infinitest test queue results", TestResultsListener.class);
}
