package org.infinitest.eclipse.util;

import static com.google.common.collect.Lists.*;
import static org.infinitest.eclipse.util.PickleJar.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class WhenStoringObjectsAsStrings
{
    @Test
    public void canPickleAnyOldObject()
    {
        String stringForm = pickle(new String("BB"));
        assertEquals(new String("BB"), unpickle(stringForm));
    }

    @Test
    public void canPickleListsOfObjects()
    {
        String stringForm = pickle(newArrayList("Hello", "There").toString());
        assertEquals(newArrayList("Hello", "There").toString(), unpickle(stringForm));
    }
}
