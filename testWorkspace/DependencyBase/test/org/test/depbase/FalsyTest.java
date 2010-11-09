package org.test.depbase;

import junit.framework.TestCase;

public class FalsyTest extends TestCase
{
    public void testShouldNotBeFalse()
    {
        assertFalse(!new Truth().value());
    }
}
