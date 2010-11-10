package org.infinitest;

import org.infinitest.parser.FakeJavaClass;
import org.infinitest.util.EqualityTestSupport;

public class JavaClassEqualityTest extends EqualityTestSupport
{
    @Override
    protected Object createEqualInstance()
    {
        return new FakeJavaClass(getClass().getName());
    }

    @Override
    protected Object createUnequalInstance()
    {
        return new FakeJavaClass(Object.class.getName());
    }

}
