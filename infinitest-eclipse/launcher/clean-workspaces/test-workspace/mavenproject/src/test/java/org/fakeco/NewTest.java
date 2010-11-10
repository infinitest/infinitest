package org.fakeco;

import org.infinitest.utils.EqualsHashCodeTestSupport;

public class NewTest extends EqualsHashCodeTestSupport {

	protected Object equal() throws Exception {
		return new Integer(1);
	}

	protected Object notEqual() throws Exception {
		return new Integer(2);
	}
}
