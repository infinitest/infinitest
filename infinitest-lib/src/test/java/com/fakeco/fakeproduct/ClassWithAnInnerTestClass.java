package com.fakeco.fakeproduct;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClassWithAnInnerTestClass
{
    public class InnerTest
    {
        @Test
        public void shouldPass()
        {
            assertTrue(true);
        }
    }
}
