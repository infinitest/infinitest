package org.infinitest.parser;

import static org.infinitest.parser.DescriptorParser.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * http://www.murrayc.com/learning/java/java_classfileformat.shtml#TypeDescriptors <br>
 * RISK Do we care about MethodDescriptors?
 */
public class WhenParsingDescriptionsPulledFromTheConstantPool
{
    @Test
    public void shouldConvertFieldDescriptorPrimitiveTypesToObject()
    {
        assertEquals(Object.class.getName(), parse("B"));
        assertEquals(Object.class.getName(), parse("C"));
        assertEquals(Object.class.getName(), parse("D"));
        assertEquals(Object.class.getName(), parse("F"));
        assertEquals(Object.class.getName(), parse("I"));
        assertEquals(Object.class.getName(), parse("J"));
        assertEquals(Object.class.getName(), parse("S"));
        assertEquals(Object.class.getName(), parse("Z"));
    }

    @Test
    public void shouldConvertArraysToSimpleClasses()
    {
        assertEquals("com.fake.Product", parse("[[Lcom/fake/Product"));
    }

    @Test
    public void shouldConvertClasses()
    {
        assertEquals(List.class.getName(), parse("Ljava/util/List"));
    }

    private String parse(String descriptor)
    {
        return parseClassNameFromConstantPoolDescriptor(descriptor);
    }
}
