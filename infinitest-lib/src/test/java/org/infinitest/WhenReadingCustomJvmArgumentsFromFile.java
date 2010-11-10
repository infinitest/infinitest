package org.infinitest;

import static com.google.common.collect.Lists.*;
import static org.infinitest.FileCustomJvmArgumentReader.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenReadingCustomJvmArgumentsFromFile
{
    private File tempDirectory;
    private File file;
    private FileCustomJvmArgumentReader reader;

    @Before
    public void setUp() throws IOException
    {
        tempDirectory = new File(System.getProperty("java.io.tmpdir"));
        assertTrue(tempDirectory.exists());
        assertTrue(tempDirectory.exists());
        file = new File(tempDirectory, FILE_NAME);
        file.createNewFile();
        assertTrue("Failed to create arguments file.", file.exists());

        reader = new FileCustomJvmArgumentReader(tempDirectory);
    }

    @After
    public void tearDown()
    {
        //assertTrue("Failed to delete arguments file.", file.delete());
        file.delete();
    }

    @Test
    public void shouldReturnEmptyListIfDirectoryDoesNotExists()
    {
        FileCustomJvmArgumentReader argReader = new FileCustomJvmArgumentReader(new File("fileThatDoesNotExist"));

        List<String> arguments = argReader.readCustomArguments();

        assertNotNull(arguments);
        assertTrue(arguments.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListIfFileIsEmpty()
    {
        List<String> arguments = reader.readCustomArguments();

        assertNotNull(arguments);
        assertTrue(arguments.isEmpty());
    }

    @Test
    public void shouldReturnArgumentsAsListIfFileHasContents() throws IOException
    {
        String singleArgument = writeArguments("-DsomeArg=foo");

        List<String> arguments = reader.readCustomArguments();

        assertEquals(newArrayList(singleArgument), arguments);
    }

    @Test
    public void shouldReturnEachArgumentAsSeparateEntryInList() throws IOException
    {
        writeArguments("-DsomeArg=foo -DanotherArg=foo");

        List<String> arguments = reader.readCustomArguments();

        assertEquals(newArrayList("-DsomeArg=foo", "-DanotherArg=foo"), arguments);
    }

    private String writeArguments(String arguments) throws IOException, FileNotFoundException
    {
        String singleArgument = arguments;
        IOUtils.write(singleArgument, new FileOutputStream(file));
        return singleArgument;
    }
}
