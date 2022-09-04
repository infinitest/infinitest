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
package org.infinitest.intellij.plugin.swingui;

import static java.util.Arrays.asList;
import static org.infinitest.testrunner.TestEvent.TestState.METHOD_FAILURE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.swing.tree.TreeModel;

import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.intellij.IntellijMockBase;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;
import org.junit.Test;

import junit.framework.AssertionFailedError;

public class WhenDisplayingResultsInATree extends IntellijMockBase {
	private static final String DEFAULT_TEST_NAME = "testName";

	private TreeModel model;
	private ResultCollector collector;

	@Before
	public void inContext() {
		InfinitestCore mockCore = mock(InfinitestCore.class);
		collector = new ResultCollector(mockCore);
		
		when(launcher.getResultCollector()).thenReturn(collector);
		
		model = new TreeModelAdapter(project);
	}

	@Test
	public void shouldHaveRootNode() {
		assertNotNull(model.getRoot());
		assertEquals(0, model.getIndexOfChild(project, module));
	}

	@Test
	public void shouldHaveChildNodesForPointsOfFailure() {
		TestEvent event = eventWithError(new AssertionFailedError());
		testRun(event);
		assertEquals(1, model.getChildCount(model.getRoot()));
		assertEquals(event.getPointOfFailure(), model.getChild(module, 0));
	}

	@Test
	public void shouldCreateNodesForEachEvent() {
		testRun(eventWithError(new AssertionFailedError()), eventWithError(new NullPointerException()));
		assertEquals(2, model.getChildCount(module));
	}

	@Test
	public void shouldHaveSubNodesForIndividualTests() {
		TestEvent event = eventWithError(new AssertionFailedError());
		testRun(event);
		Object pointOfFailureNode = model.getChild(module, 0);
		assertEquals(1, model.getChildCount(pointOfFailureNode));
		assertEquals(event, model.getChild(pointOfFailureNode, 0));
	}

	@Test
	public void shouldProvideIndexOfNodes() {
		testRun(eventWithError(new AssertionFailedError()), eventWithError(new NullPointerException()));
		assertNodeReferenceIntegrity(module, 0);
		assertNodeReferenceIntegrity(module, 1);
	}

	@Test
	public void shouldIdentifyOnlyTestNodesAsLeaves() {
		assertTrue(model.isLeaf(module));

		testRun(eventWithError(new AssertionFailedError()));
		assertFalse(model.isLeaf(module));

		Object failureNode = model.getChild(module, 0);
		assertEquals(1, model.getChildCount(failureNode));
		assertFalse(model.isLeaf(failureNode));

		Object testNode = model.getChild(failureNode, 0);
		assertEquals(0, model.getChildCount(testNode));
		assertTrue(model.isLeaf(testNode));
	}

	private void assertNodeReferenceIntegrity(Object parent, int nodeIndex) {
		assertEquals(nodeIndex, model.getIndexOfChild(parent, model.getChild(parent, nodeIndex)));
	}

	private static TestEvent eventWithError(Throwable error) {
		return new TestEvent(METHOD_FAILURE, "", "", "", error);
	}

	private void testRun(TestEvent... events) {
		String testName = DEFAULT_TEST_NAME;
		if (events.length != 0) {
			testName = events[0].getTestName();
		}
		collector.testCaseComplete(new TestCaseEvent(testName, this, new TestResults(asList(events))));
	}
}
