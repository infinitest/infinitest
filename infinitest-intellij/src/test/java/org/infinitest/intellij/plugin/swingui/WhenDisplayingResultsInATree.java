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

import static java.util.Arrays.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.swing.event.*;
import javax.swing.tree.*;

import org.infinitest.*;
import org.infinitest.testrunner.*;
import org.junit.*;
import org.junit.Test;

public class WhenDisplayingResultsInATree implements TreeModelListener {
	private static final String DEFAULT_TEST_NAME = "testName";

	private TreeModel model;
	private List<TreeModelEvent> treeEvents;
	private ResultCollector collector;

	@Before
	public void inContext() {
		InfinitestCore mockCore = mock(InfinitestCore.class);
		collector = new ResultCollector(mockCore);
		model = new TreeModelAdapter(collector);
		treeEvents = new ArrayList<TreeModelEvent>();
	}

	@Test
	public void shouldHaveRootNode() {
		assertNotNull(model.getRoot());
	}

	@Test
	public void shouldHaveChildNodesForPointsOfFailure() {
		TestEvent event = eventWithError(new AssertionError());
		testRun(event);
		assertEquals(1, model.getChildCount(model.getRoot()));
		assertEquals(event.getPointOfFailure(), model.getChild(model.getRoot(), 0));
	}

	@Test
	public void shouldCreateNodesForEachEvent() {
		testRun(eventWithError(new AssertionError()), eventWithError(new NullPointerException()));
		assertEquals(2, model.getChildCount(model.getRoot()));
	}

	@Test
	public void shouldHaveSubNodesForIndividualTests() {
		TestEvent event = eventWithError(new AssertionError());
		testRun(event);
		Object pointOfFailureNode = model.getChild(model.getRoot(), 0);
		assertEquals(1, model.getChildCount(pointOfFailureNode));
		assertEquals(event, model.getChild(pointOfFailureNode, 0));
	}

	@Test
	public void shouldProvideIndexOfNodes() {
		testRun(eventWithError(new AssertionError()), eventWithError(new NullPointerException()));
		assertNodeReferenceIntegrity(model.getRoot(), 0);
		assertNodeReferenceIntegrity(model.getRoot(), 1);
	}

	@Test
	public void shouldIdentifyOnlyTestNodesAsLeaves() {
		assertTrue(model.isLeaf(model.getRoot()));

		testRun(eventWithError(new AssertionError()));
		assertFalse(model.isLeaf(model.getRoot()));

		Object failureNode = model.getChild(model.getRoot(), 0);
		assertEquals(1, model.getChildCount(failureNode));
		assertFalse(model.isLeaf(failureNode));

		Object testNode = model.getChild(failureNode, 0);
		assertEquals(0, model.getChildCount(testNode));
		assertTrue(model.isLeaf(testNode));
	}

	private void assertNodeReferenceIntegrity(Object parent, int nodeIndex) {
		assertEquals(nodeIndex, model.getIndexOfChild(parent, model.getChild(parent, nodeIndex)));
	}

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		throw new UnsupportedOperationException("should never be called");
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		throw new UnsupportedOperationException("should never be called");
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		throw new UnsupportedOperationException("should never be called");
	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		treeEvents.add(e);
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
