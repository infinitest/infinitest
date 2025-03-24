package totest;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/** testclass for playing around with TestNG groups */
public class TestWithTestNG {

	@Test
	public void hallo() {
		// left is calculated result, right is expected result
		assertEquals("actual", "actual");
	}

	@Test(groups = { "slow" })
	public void shouldNotBeTestedGroup() {
		assertTrue(false);
		assertFalse("h".equals("h"));
	}

	@Test(groups = { "manual" })
	public void shouldNotBeTestedGroup3() {
		assertTrue("I'm never called ".length() > 2);
		assertFalse(true);
	}

	@Test(groups = { "shouldbetested" })
	public void doSomeTest() {
		long nano = System.nanoTime();
		long nano2 = System.nanoTime();
		assertTrue(nano2 >= nano);
	}

	@Test(groups = { "mixed", "slow" })
	public void shouldNotBeTestedGroup2() {
		assertFalse(true);
	}

	@Test(groups = { "green" }, dependsOnGroups = { "slow" })
	public void shouldNoBeTestedDueToDependencyOnFilteredGroup() {
		assertFalse(true);
	}
}
