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
package org.infinitest.eclipse;

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class InfinitestCoreClasspath
{
    private static File jarFile;

    public static File getCoreJarLocation(InfinitestPlugin plugin)
    {
        if (jarFile == null || !jarFile.exists())
        {
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            jarFile = getNonConflictingJarFile(tempDir);
            if (!jarFile.getParentFile().exists())
            {
                log(SEVERE, "No parent directory for " + jarFile);
                throw new IllegalStateException("No parent directory for " + jarFile);
            }
            writeJar(jarFile, plugin);
        }
        return jarFile;
    }

    private static File getNonConflictingJarFile(File tempDir)
    {
        File proposedFile = new File(tempDir.getAbsolutePath() + "/infinitest.jar");
        while (proposedFile.exists())
        {
            proposedFile = new File(tempDir.getAbsolutePath() + "/infinitest" + randInt() + ".jar");
        }
        proposedFile.deleteOnExit();
        return proposedFile;
    }

    private static int randInt()
    {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    private static void writeJar(File coreJarLocation, InfinitestPlugin plugin)
    {
        // Only for tests ->
        if (plugin != null)
        // <-
        {
            Enumeration<?> e = plugin.getPluginBundle().findEntries("", "*infinitest-runner*.jar", true);

            if (e == null)
            {
                log(SEVERE, "Error creating testrunner classpath. Cannot find infinitest core bundle");
            }
            else
            {
                while (e.hasMoreElements())
                {
                    URL resource = (URL) e.nextElement();
                    try
                    {
                        InputStream in = resource.openStream();
                        copyFile(in, coreJarLocation);
                    }
                    catch (IOException e1)
                    {
                        log(SEVERE, "Error creating testrunner classpath. Could not write to " + coreJarLocation);
                        throw new RuntimeException(e1);
                    }
                }
            }
        }
    }

    private static void copyFile(InputStream fis, File out) throws IOException
    {
        if (!out.exists())
        {
            out.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[1024];
        int i;
        while ((i = fis.read(buf)) != -1)
        {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }
}
