package org.infinitest.parser;

class DisposedClassException extends IllegalStateException
{
    private static final long serialVersionUID = 6489119088334173457L;

    public DisposedClassException(String name)
    {
        super(name + " has already been disposed. Cannot provide imports");
    }
}
