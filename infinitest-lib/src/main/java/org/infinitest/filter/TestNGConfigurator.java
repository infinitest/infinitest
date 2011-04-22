/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2011
 * "Matthias Droste" <matthias.droste@gmail.com>,
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
package org.infinitest.filter;

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.infinitest.TestNGConfiguration;
import org.infinitest.TestQueueEvent;
import org.infinitest.TestQueueListener;

public class TestNGConfigurator implements TestQueueListener
{
    private static final String EXCLUDED_GROUPS = "excluded-groups=";
    private final File file;
    private final TestNGConfiguration testNGConfiguration;

    public TestNGConfigurator(File filterFile)
    {
        testNGConfiguration = TestNGConfiguration.INSTANCE;
        file = filterFile;
        if (!file.exists())
        {
            log(INFO, "Filter file " + file + " does not exist.");
        }

        updateFilterList();
    }

    public void updateFilterList()
    {
        if (file == null)
        {
            return;
        }

        if (file.exists())
        {
            tryToReadFilterFile();
        }
    }

    private void tryToReadFilterFile()
    {
        try
        {
            readFilterFile();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Something horrible happened to the filter file", e);
        }
    }

    private void readFilterFile() throws IOException
    {
        FileReader fileReader = new FileReader(file);
        try
        {
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            do
            {
                line = reader.readLine();
                addFilter(line);
            } while (line != null);
        }
        finally
        {
            fileReader.close();
        }
    }

    private void addFilter(String line)
    {
        if (StringUtils.startsWith(line, "##"))
        {
            if (line.contains(EXCLUDED_GROUPS))
            {
                testNGConfiguration.setExcludedGroups(StringUtils.substringAfter(line, EXCLUDED_GROUPS).trim());
            }
        }

    }

    public void reloading()
    {
        TestNGConfiguration.INSTANCE.clear();
        updateFilterList();
    }

    public void testQueueUpdated(TestQueueEvent event)
    {
        // TODO Auto-generated method stub
    }

    public void testRunComplete()
    {
        // TODO Auto-generated method stub
    }
}
