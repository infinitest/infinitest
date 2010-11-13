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
package org.infinitest.maven;

import static org.apache.commons.io.FileUtils.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal process-lib-dir
 */
public class ProcessLibDirectoryMojo extends AbstractMojo
{
    /**
     * The base directory.
     * 
     * @parameter expression="${basedir}"
     * @required
     */
    File baseDirectory;

    /**
     * The target directory.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    File targetDirectory;

    public void execute() throws MojoExecutionException
    {
        try
        {
            File libDir = new File(baseDirectory + "/lib");
            if (!libDir.exists() || !libDir.isDirectory())
            {
                getLog().warn("There is no lib directory present in this project");
                return;
            }

            String[] jarFileNames = getLibPathedJarFileNames(libDir);
            addJarsToBundleClasspath(jarFileNames);
            addJarsToBuildProperties(jarFileNames);
        }
        catch (IOException ioe)
        {
            throw new MojoExecutionException("IOException thrown: ", ioe);
        }
    }

    private void addJarsToBundleClasspath(String[] jarFileNames) throws IOException
    {
        File bndFile = new File(baseDirectory + "/osgi.bnd");
        if (bndFile.exists())
        {
            copyFileToDirectory(bndFile, targetDirectory);
        }

        FileWriter bndWriter = new FileWriter(new File(targetDirectory + "/osgi.bnd"), true);

        try
        {
            bndWriter.append("\nBundle-Classpath: .");
            for (String fileName : jarFileNames)
            {
                bndWriter.append(",\\\n  " + fileName);
            }
        }
        finally
        {
            bndWriter.close();
        }
    }

    private void addJarsToBuildProperties(String[] jarFileNames) throws IOException
    {
        FileWriter buildPropertiesWriter = new FileWriter(new File(baseDirectory + "/build.properties"), false);

        try
        {
            buildPropertiesWriter.append("source.. = src/main/java/,src/main/resources/\n"
                            + "bin.includes = plugin.xml,preferences.ini,META-INF/," + ".,src/main/resources/icons/");

            for (String fileName : jarFileNames)
            {
                buildPropertiesWriter.append("," + fileName);
            }
            buildPropertiesWriter.append("\n");

            buildPropertiesWriter.append("jars.compile.order = .");
            for (String fileName : jarFileNames)
            {
                buildPropertiesWriter.append("," + fileName);
            }
            buildPropertiesWriter.append("\n");
        }
        finally
        {
            buildPropertiesWriter.close();
        }
    }

    private String[] getLibPathedJarFileNames(File libDir)
    {
        File[] jarFiles = libDir.listFiles((FileFilter) new SuffixFileFilter(".jar"));
        String[] libPathedJarFileNames = new String[jarFiles.length];

        for (int i = 0; i < jarFiles.length; i++)
        {
            libPathedJarFileNames[i] = "lib/" + jarFiles[i].getName();
        }

        return libPathedJarFileNames;
    }
}
