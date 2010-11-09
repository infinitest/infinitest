package org.fakeco;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("unused")
public class SlowTest {
	@Test
	public void shouldBeAbleToInterruptTestsCleanly() throws InterruptedException {
		// TODO We should be able to cleanly interrupt the running of this test
		// If you make it fail, then change it quickly to make it pass (before it finishes running)
		// it should re-run the test and remove the marker
	    //Thread.sleep(1000);
		//fail();
	}
}