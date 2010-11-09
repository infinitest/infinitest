package org.test;

import static org.junit.Assert.fail;

import org.junit.Test;


public class FilteredFailingTest {
	@Test
	public void shouldFail()
	{
		// TODO - This failure should be filtered out by the filter file.
		fail();
		// You should be able to re-add / remove it by changing the file and then changing the test
	}
}
