/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.intellij.plugin.swingui;

import static java.util.Arrays.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.swing.event.*;
import javax.swing.tree.*;

import junit.framework.*;

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
		TestEvent event = eventWithError(new AssertionFailedError());
		testRun(event);
		assertEquals(1, model.getChildCount(model.getRoot()));
		assertEquals(event.getPointOfFailure(), model.getChild(model.getRoot(), 0));
	}

	@Test
	public void shouldCreateNodesForEachEvent() {
		testRun(eventWithError(new AssertionFailedError()), eventWithError(new NullPointerException()));
		assertEquals(2, model.getChildCount(model.getRoot()));
	}

	@Test
	public void shouldHaveSubNodesForIndividualTests() {
		TestEvent event = eventWithError(new AssertionFailedError());
		testRun(event);
		Object pointOfFailureNode = model.getChild(model.getRoot(), 0);
		assertEquals(1, model.getChildCount(pointOfFailureNode));
		assertEquals(event, model.getChild(pointOfFailureNode, 0));
	}

	@Test
	public void shouldFireTreeStructureChangedWhenNewEventIsAdded() {
		model.addTreeModelListener(this);
		testRun(eventWithError(new AssertionFailedError()));
		assertEquals(2, treeEvents.size());

		model.removeTreeModelListener(this);
		testRun(eventWithError(new AssertionFailedError()));
		assertEquals(2, treeEvents.size());
	}

	@Test
	public void shouldProvideIndexOfNodes() {
		testRun(eventWithError(new AssertionFailedError()), eventWithError(new NullPointerException()));
		assertNodeReferenceIntegrity(model.getRoot(), 0);
		assertNodeReferenceIntegrity(model.getRoot(), 1);
	}

	@Test
	public void shouldIdentifyOnlyTestNodesAsLeaves() {
		assertTrue(model.isLeaf(model.getRoot()));

		testRun(eventWithError(new AssertionFailedError()));
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

	public void treeNodesChanged(TreeModelEvent e) {
		throw new UnsupportedOperationException("should never be called");
	}

	public void treeNodesInserted(TreeModelEvent e) {
		throw new UnsupportedOperationException("should never be called");
	}

	public void treeNodesRemoved(TreeModelEvent e) {
		throw new UnsupportedOperationException("should never be called");
	}

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
