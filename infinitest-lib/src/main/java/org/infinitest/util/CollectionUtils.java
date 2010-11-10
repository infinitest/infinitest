package org.infinitest.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionUtils
{
    public static <T> T first(Collection<T> list)
    {
        Iterator<T> iterator = list.iterator();
        if (iterator.hasNext())
            return iterator.next();
        return null;
    }
}
