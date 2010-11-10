package com.fakeco.fakeproduct;

import org.junit.internal.builders.IgnoredClassRunner;

public class CustomRunner extends IgnoredClassRunner
{
    public CustomRunner(Class<?> klass)
    {
        super(klass);
    }
}
