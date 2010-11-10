package org.infinitest.parser;

import java.io.IOException;

class MalformedClassFileException extends IOException
{
    private static final long serialVersionUID = -1L;

    public MalformedClassFileException(String fileName)
    {
        super(fileName);
    }
}
