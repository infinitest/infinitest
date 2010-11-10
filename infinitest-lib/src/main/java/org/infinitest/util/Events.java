package org.infinitest.util;

import static com.google.common.collect.Lists.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Events<T>
{
    private final Method eventMethod;
    private final List<T> listeners = newArrayList();

    public Events(Method eventMethod)
    {
        this.eventMethod = eventMethod;
    }

    public void addListener(T listener)
    {
        listeners.add(listener);
    }

    public void fire(Object... eventData)
    {
        for (T each : listeners)
        {
            try
            {
                eventMethod.invoke(each, eventData);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void removeListener(T listener)
    {
        listeners.remove(listener);
    }

    public static <T> Events<T> eventFor(Class<T> listenerClass)
    {
        Method method = listenerClass.getDeclaredMethods()[0];
        return new Events<T>(method);
    }
}
