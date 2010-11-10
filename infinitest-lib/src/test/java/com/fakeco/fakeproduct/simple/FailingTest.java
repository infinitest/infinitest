package com.fakeco.fakeproduct.simple;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.infinitest.util.InfinitestTestUtils;
import org.junit.Test;

public class FailingTest
{
    @Test
    public void shouldFail()
    {
        assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
        fail();
    }
}