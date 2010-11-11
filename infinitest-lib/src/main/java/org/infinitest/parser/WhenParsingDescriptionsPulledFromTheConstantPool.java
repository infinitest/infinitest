/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
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
