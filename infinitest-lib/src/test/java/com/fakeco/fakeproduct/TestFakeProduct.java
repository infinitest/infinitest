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
package com.fakeco.fakeproduct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * This is a test who's behavior can be externally controlled using a set of static methods.
 * 
 * It uses a property file because there's no way to get the instance of the class that JUnit
 * creates and we couldn't use a static variable because JUnit (used to!) use a different
 * classloader.
 * 
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 * 
 */
public class TestFakeProduct extends TestCase
{
    private static final String CALLCOUNT_POSTFIX = ".callcount";
    private static final String EXCEPTION_POSTFIX = ".exception";
    @SuppressWarnings("unused")
    private FakeProduct fakeProduct = null;
    private static File statFailureStateFile = new File("TestFakeProduct.data");

    @Override
    protected void setUp() throws Exception
    {
        if (!statFailureStateFile.exists())
        {
            setUpState();
            statFailureStateFile.deleteOnExit();
        }
        fakeProduct = new FakeProduct();
        runTestMethod("setUp");
    }

    @Override
    protected void tearDown() throws Exception
    {
        fakeProduct = null;
        runTestMethod("tearDown");
    }

    public void testNumber1() throws Exception
    {
        runTestMethod("testNumber1");
        @SuppressWarnings("unused")
        Object o = new Runnable()
        {
            public void run()
            {
                throw new IllegalStateException("This is an inner class and should not be run");
            }
        };
    }

    public void testNumber2() throws Exception
    {
        runTestMethod("testNumber2");
    }

    public static void setTestSuccess(String testName, String failureMessage, boolean pass)
                    throws FileNotFoundException, IOException
    {
        if (pass)
        {
            writeProperty(testName, "");
        }
        else
        {
            writeProperty(testName, failureMessage);
        }
    }

    public static int getCallCount(String methodName) throws FileNotFoundException, IOException
    {
        Properties prop = loadProperties();
        String callCount = prop.getProperty(methodName + CALLCOUNT_POSTFIX);
        if (callCount == null)
        {
            return 0;
        }
        return Integer.parseInt(callCount);
    }

    public static void setUpState() throws IOException
    {
        tearDownState();
        assertTrue("Could not re-initialize test state", statFailureStateFile.createNewFile());
    }

    public static void tearDownState()
    {
        if (statFailureStateFile.exists())
        {
            statFailureStateFile.deleteOnExit();
            statFailureStateFile.delete();
        }
    }

    private void runTestMethod(String methodName) throws Exception
    {
        int callcount = getCallCount(methodName);
        writeProperty(methodName + CALLCOUNT_POSTFIX, Integer.toString(callcount + 1));
        String message = readProperty(methodName);
        if (message != null && !message.equals(""))
        {
            if (message.endsWith(EXCEPTION_POSTFIX))
            {
                String exception = message.replace(EXCEPTION_POSTFIX, "");
                Class<?> clazz = Class.forName(exception);
                throw (Exception) clazz.newInstance();
            }
            fail(message);
        }
    }

    private static Properties loadProperties() throws FileNotFoundException, IOException
    {
        if (!statFailureStateFile.exists())
        {
            throw new IllegalStateException("You must call setUpState before using other methods on this class");
        }
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(statFailureStateFile);
        try
        {
            properties.load(inputStream);
        }
        finally
        {
            inputStream.close();
        }
        return properties;
    }

    private String readProperty(String testName) throws FileNotFoundException, IOException
    {
        Properties prop = loadProperties();
        String message = prop.getProperty(testName);
        return message;
    }

    private static void writeProperty(String key, String value) throws FileNotFoundException, IOException
    {
        if (!statFailureStateFile.exists())
        {
            throw new IllegalStateException("You must call clearState before using other methods on this class");
        }
        Properties properties = loadProperties();
        properties.setProperty(key, value);
        FileOutputStream fostream = new FileOutputStream(statFailureStateFile);
        properties.store(fostream, "");
        fostream.close();
    }

    public static void setTestError(String testName, Class<?> exceptionClass) throws FileNotFoundException, IOException
    {
        if (exceptionClass == null)
        {
            writeProperty(testName, "");
        }
        else
        {
            writeProperty(testName, exceptionClass.getName() + EXCEPTION_POSTFIX);
        }
    }
}
