package com.fakeco.fakeproduct;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
@ClassAnnotation
@InvisibleClassAnnotation
public class AnnotatedClass
{
    @SuppressWarnings("all")
    @MethodAnnotation
    @Test
    private void aMethod(@ParameterAnnotation(value = 1, otherValue = 2) String aVariable,
                    @InvisibleParameterAnnotation String anotherVariable)
    {
    }

    @Ignore
    @Test
    public void shouldaction()
    {
        fail();
    }
}
