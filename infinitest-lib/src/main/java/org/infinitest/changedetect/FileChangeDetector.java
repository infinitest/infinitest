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
package org.infinitest.changedetect;

import static java.lang.Character.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.infinitest.ClasspathProvider;
import org.infinitest.util.InfinitestUtils;

public class FileChangeDetector implements ChangeDetector
{
    private Map<File, Long> timestampIndex;
    private File[] classDirectories;

    public FileChangeDetector()
    {
        classDirectories = new File[0];
        clear();
    }

    public void setClasspathProvider(ClasspathProvider classpath)
    {
        clear();
        List<File> classDirs = classpath.classDirectoriesInClasspath();
        classDirectories = classDirs.toArray(new File[classDirs.size()]);
    }

    public synchronized Set<File> findChangedFiles() throws IOException
    {
        return findFiles(classDirectories, false);
    }

    private Set<File> findFiles(File[] classesOrDirectories, boolean isPackage) throws IOException
    {
        Set<File> changedFiles = new HashSet<File>();
        for (File classFileOrDirectory : classesOrDirectories)
        {
            if (classFileOrDirectory.isDirectory() && hasValidName(classFileOrDirectory, isPackage))
            {
                findChildren(changedFiles, classFileOrDirectory);
            }
            else if (ClassFileFilter.isClassFile(classFileOrDirectory))
            {
                File classFile = classFileOrDirectory;
                Long timestamp = timestampIndex.get(classFile);
                if (timestamp == null || getModificationTimestamp(classFile) != timestamp)
                {
                    timestampIndex.put(classFile, getModificationTimestamp(classFile));
                    changedFiles.add(classFile);
                    InfinitestUtils.log(Level.FINEST, "Class file added to changelist " + classFile);
                }
            }
        }
        return changedFiles;
    }

    private void findChildren(Set<File> changedFiles, File classFileOrDirectory) throws IOException
    {
        File[] children = childrenOf(classFileOrDirectory);
        if (children != null)
        {
            changedFiles.addAll(findFiles(children, true));
        }
    }

    protected File[] childrenOf(File directory)
    {
        return directory.listFiles(new ClassFileFilter());
    }

    private boolean hasValidName(File classfileOrDirectory, boolean isPackage)
    {
        return !isPackage || isLetter(classfileOrDirectory.getName().charAt(0));
    }

    protected long getModificationTimestamp(File classFile)
    {
        return classFile.lastModified();
    }

    public synchronized void clear()
    {
        timestampIndex = new HashMap<File, Long>();
    }

    private Set<File> findRemovedFiles()
    {
        Set<File> removedFiles = new HashSet<File>();
        for (File key : timestampIndex.keySet())
        {
            if (!key.exists())
            {
                removedFiles.add(key);
            }
        }
        return removedFiles;
    }

    public synchronized boolean filesWereRemoved()
    {
        return !findRemovedFiles().isEmpty();
    }
}
