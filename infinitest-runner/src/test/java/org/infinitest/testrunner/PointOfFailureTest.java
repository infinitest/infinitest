package org.infinitest.testrunner;

import static org.junit.Assert.*;

import org.infinitest.util.EqualityTestSupport;
import org.junit.Test;

public class PointOfFailureTest extends EqualityTestSupport
{
    @Test
    public void shouldBeEqualIfExactMatch()
    {
        PointOfFailure first = new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "");
        PointOfFailure second = new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "");

        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first, second);
    }

    @Test
    public void shouldNotBeEqualIfAnyFieldDiffers()
    {
        PointOfFailure pointOfFailure = new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "");

        PointOfFailure differentTestClass = new PointOfFailure("SomeOtherTest", 1,
                        NullPointerException.class.getName(), "");
        assertFalse(pointOfFailure.equals(differentTestClass));

        PointOfFailure differentLineNumber = new PointOfFailure("SomeTest", 2, NullPointerException.class.getName(), "");
        assertFalse(pointOfFailure.equals(differentLineNumber));

        PointOfFailure differentErrorClass = new PointOfFailure("SomeTest", 1, AssertionError.class.getName(), "");
        assertFalse(pointOfFailure.equals(differentErrorClass));

        PointOfFailure differentMessage = new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), "foo");
        assertFalse(pointOfFailure.equals(differentMessage));
    }

    @Test
    public void shouldProduceRepresentativeToString()
    {
        PointOfFailure pointOfFailure = new PointOfFailure("org.infinitest.SomeTest", 1,
                        NullPointerException.class.getSimpleName(), "message");

        String expected = "org.infinitest.SomeTest:1 - NullPointerException(message)";
        assertEquals(expected, pointOfFailure.toString());
    }

    @Test
    public void shouldProvideLineNumberAndClassName()
    {
        String className = "org.infinitest.SomeTest";
        PointOfFailure pointOfFailure = new PointOfFailure(className, 1, "", "");
        assertEquals(1, pointOfFailure.getLineNumber());
        assertEquals(className, pointOfFailure.getClassName());
    }

    @Test
    public void shouldProduceRepresentativeToStringWithoutMessage()
    {
        PointOfFailure pointOfFailure = new PointOfFailure("org.infinitest.SomeTest", 1,
                        NullPointerException.class.getSimpleName(), null);

        String expected = "org.infinitest.SomeTest:1 - NullPointerException";
        assertEquals(expected, pointOfFailure.toString());
    }

    @Override
    protected Object createEqualInstance()
    {
        return new PointOfFailure("SomeTest", 1, NullPointerException.class.getName(), null);
    }

    @Override
    protected Object createUnequalInstance()
    {
        return new PointOfFailure("SomeTest", 1, RuntimeException.class.getName(), null);
    }
}
