package com.fakeco.fakeproduct;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestJUnit4TestCase
{
    private static final String KEY = TestJUnit4TestCase.class.getName() + "TOGGLE";
    private static final String ENABLED = "ENABLED";

    @Test
    @SuppressWarnings("all")
    public void shouldPass()
    {
    }

    @Test
    public void shouldFailIfPropertyIsSet()
    {
        if (ENABLED.equals(System.getProperty(KEY)))
        {
            fail("Test Failed");
        }
    }

    public static void enable()
    {
        System.setProperty(KEY, ENABLED);
    }

    public static void disable()
    {
        System.clearProperty(KEY);
    }
}
