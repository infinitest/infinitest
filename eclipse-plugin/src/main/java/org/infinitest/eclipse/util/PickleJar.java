package org.infinitest.eclipse.util;

import static org.apache.commons.lang.SerializationUtils.*;

import java.io.Serializable;

public abstract class PickleJar
{
    public static String pickle(Serializable object)
    {
        return new String (serialize(object));
    }

    public static Serializable unpickle(String stringForm)
    {
        return (Serializable) deserialize(stringForm.getBytes());
    }
}
