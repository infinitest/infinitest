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
package org.infinitest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.xml.XmlSuite;

public class TestNGConfigurator
{
    private static final String SUFFIX = "\\s?=\\s?(.+)";
    private static final String PREFIX = "^\\s*#+\\s?";
    private static final String EXCLUDED_GROUPS = "excluded-groups";
    private static final String INCLUDED_GROUPS = "groups";
    private static final String SUITE_XML_FILE = "suiteXmlFile";
    private static final String LISTENERS = "listeners";
    private static final Pattern EXCLUDED = Pattern.compile(PREFIX + EXCLUDED_GROUPS + SUFFIX);
    private static final Pattern INCLUDED = Pattern.compile(PREFIX + INCLUDED_GROUPS + SUFFIX);
    private static final Pattern SUITE = Pattern.compile(PREFIX + SUITE_XML_FILE + SUFFIX);
    private static final Pattern LISTENER = Pattern.compile(PREFIX + LISTENERS + SUFFIX);;
    private static final File FILTERFILE = new File("infinitest.filters");

    private final TestNGConfiguration testNGConfiguration;
    private File file = null;

    public TestNGConfigurator()
    {
        testNGConfiguration = new TestNGConfiguration();
        if (file == null)
        {
            file = FILTERFILE;
        }
        updateFilterList();
    }

    public TestNGConfigurator(File filterFile)
    {
        testNGConfiguration = new TestNGConfiguration();
        file = filterFile;

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

    public TestNGConfiguration getConfig()
    {
        return testNGConfiguration;
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
                if (line != null)
                {
                    addFilter(line);
                }
            } while (line != null);
        }
        finally
        {
            fileReader.close();
        }
    }

    private void addFilter(String line)
    {
        Matcher matcher = EXCLUDED.matcher(line.trim());
        if (matcher.matches())
        {
            String excludedGroups = matcher.group(1);
            testNGConfiguration.setExcludedGroups(excludedGroups);
        }
        else
        {
            matcher = INCLUDED.matcher(line);
            if (matcher.matches())
            {
                String includedGroups = matcher.group(1).trim();
                testNGConfiguration.setGroups(includedGroups);
            }
            else
            {
                matcher = SUITE.matcher(line);
                if (matcher.matches())
                {
                    final XmlSuite suite = createSuite(matcher.group(1).trim());
                    testNGConfiguration.setSuite(suite);
                }
                else
                {
                    matcher = LISTENER.matcher(line);
                    if (matcher.matches())
                    {
                        final List<Object> listenerList = createListenerList(matcher.group(1).trim());
                        testNGConfiguration.setListeners(listenerList);
                    }
                }
            }
        }
    }

    private List<Object> createListenerList(String listeners)
    {
        final String[] listenerTypes = listeners.split("\\s*,\\s*");
        final List<Object> listenerList = new ArrayList<Object>();
        for (final String listenername : listenerTypes)
        {
            try
            {
                listenerList.add(Class.forName(listenername).newInstance());
            }
            catch (final ReflectiveOperationException e)
            {
                // unable to add this listener, just continue with the next.
            }
        }
        return listenerList;
    }

    private XmlSuite createSuite(String filename)
    {
        final List<String> suiteFiles = new ArrayList<String>();
        suiteFiles.add(filename);
        final XmlSuite suite = new XmlSuite();
        suite.setSuiteFiles(suiteFiles);
        return suite;
    }
}
