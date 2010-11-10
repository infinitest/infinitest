package org.test;

import junit.framework.TestCase;

public class JUnit3Test extends TestCase {
	
	public void testThatShouldFailInExternalClass()
	{
		// TODO Uncommenting this should cause the failure marker to occur on this line, not in ArrayList
		//new java.util.ArrayList<Object>(null);
	}
	
	public void testThatFailsInAnInnerClass() throws Exception {
		Runnable runnable = new Runnable()
		{
			public void run() {
				// TODO Uncommenting this will cause a marker to be placed on the runnable.run()
				// Ideally, however, we'd like it to be on the fail().
				//org.junit.Assert.fail();
			}
		};
		runnable.run();
	}
}
