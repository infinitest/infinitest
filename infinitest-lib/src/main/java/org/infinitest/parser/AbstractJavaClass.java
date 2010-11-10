package org.infinitest.parser;

public abstract class AbstractJavaClass implements JavaClass
{
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof JavaClass)
        {
            JavaClass other = (JavaClass) obj;
            return other.getName().equals(getName());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    public void dispose()
    {
    }
}
