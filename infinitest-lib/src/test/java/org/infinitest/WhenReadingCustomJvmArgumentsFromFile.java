/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest;

import static com.google.common.base.Charsets.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.io.Files.*;
import static org.infinitest.FileCustomJvmArgumentReader.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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
        // assertTrue("Failed to delete arguments file.", file.delete());
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
        write(singleArgument, file, UTF_8);
        return singleArgument;
    }
}
