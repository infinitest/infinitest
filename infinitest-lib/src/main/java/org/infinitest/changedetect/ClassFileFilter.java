package org.infinitest.changedetect;

import java.io.File;
import java.io.FileFilter;

class ClassFileFilter implements FileFilter
{
    public boolean accept(File pathname)
    {
        return isClassFile(pathname) || pathname.isDirectory();
    }

    public static boolean isClassFile(File pathname)
    {
        return pathname.getAbsolutePath().matches(".*\\.[Cc][Ll][Aa][Ss][Ss]\\z");
    }
}
