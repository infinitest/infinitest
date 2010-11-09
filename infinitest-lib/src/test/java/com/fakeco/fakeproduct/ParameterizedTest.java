package com.fakeco.fakeproduct;

import java.util.Collection;
import java.util.Collections;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterizedTest
{
    public ParameterizedTest(String infix, String expectedPostfix)
    {
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Collections.emptyList();
    }
}
