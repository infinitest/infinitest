package org.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.test.depbase.Truth;

public class JUnit4Test
{
	@Test
	public void shouldPass()
	{
		assertTrue(new Truth().value());
	}

	public void shouldNotBeRun()
	{
		fail();
	}
}
