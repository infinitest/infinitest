package org.infinitest.eclipse.util;

import static com.google.common.collect.Lists.newArrayList;
import static org.infinitest.eclipse.util.PickleJar.pickle;
import static org.infinitest.eclipse.util.PickleJar.unpickle;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WhenStoringObjectsAsStrings
{
    @Test
    public void canPickleAnyOldObject()
    {
        String stringForm = pickle(new String("BB"));
        assertEquals(new String("BB"),unpickle(stringForm));
    }

	@Test
    public void canPickleListsOfObjects()
    {
        String stringForm = pickle(newArrayList("Hello", "There").toString());
        assertEquals(newArrayList("Hello", "There").toString(),unpickle(stringForm));
    }
}
