package org.infinitest.testrunner;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.experimental.categories.*;

public class FailingTestsWithCategories {

	interface IgnoreMe {
	};

	interface IgnoreMeToo {
	};

	@Test
	public void shouldBeTested() {
		fail();
	}

	@Category(IgnoreMe.class)
	@Test
	public void shouldNotBeTested() {
		fail();
	}

	@Category(IgnoreMeToo.class)
	@Test
	public void shouldAlsoNotBeTested() {
		fail();
	}
}
