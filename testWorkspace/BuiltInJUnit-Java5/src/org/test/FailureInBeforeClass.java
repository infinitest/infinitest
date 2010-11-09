package org.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.test.depbase.Truth;

public class FailureInBeforeClass {
	private static Truth truth;

	@BeforeClass
	public static void beforeClass() {
		truth = new Truth();
		// FIXME Uncommenting this should cause the test to fail
		throw new IllegalStateException();
	}
	
	@Test
	public void passingTest() throws Exception {
		assertNotNull("beforeClass was never run", truth);
	}
}
