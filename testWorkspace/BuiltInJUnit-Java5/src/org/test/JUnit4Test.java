package org.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.test.depbase.Truth;

public class JUnit4Test
{
	@Test
	public void shouldPass() throws InterruptedException
	{
		assertTrue(new Truth().value());
	}
	
	@Test
	public void shouldHandleLargeStackTraces()
	{
		// TODO Uncommenting this should produce a stack track that doesn't cause problems
		// recurse(new ArrayList<Integer>()); 
	}

	@SuppressWarnings("unused")
	private void recurse(ArrayList<Integer> arrayList) {
		arrayList.add(arrayList.size());
		recurse(arrayList);
	}

	public void shouldNotBeRun()
	{
		fail();
	}
}
