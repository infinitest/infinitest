package org.fakeco;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestWithRules {
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Test
	public void shouldFail() {
		exceptionRule.expect(NullPointerException.class);
		exceptionRule.expectMessage(CoreMatchers.equalTo("My Message"));
		throw new NullPointerException("My Message");
	}
}
