package org.infinitest.parser;

class DisposedClassException extends IllegalStateException
{
    public DisposedClassException(String name)
    {
        super(name + " has already been disposed. Cannot provide imports");
    }
}
