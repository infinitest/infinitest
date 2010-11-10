package org.infinitest.parser;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javassist.NotFoundException;

import org.junit.Test;

public class WhenClassFilesCannotBeFound
{
    @Test
    public void shouldReturnNullIfClassDissapearsWhileParsing() throws IOException
    {
        ClassParser mockParser = createMock(ClassParser.class);
        JavaClassBuilder builder = new JavaClassBuilder(mockParser);
        NotFoundException cause = new NotFoundException("");
        expect(mockParser.parse((File) anyObject())).andThrow(new RuntimeException(cause));
        replay(mockParser);
        assertNull(builder.loadClass(new File("")));
    }
}
