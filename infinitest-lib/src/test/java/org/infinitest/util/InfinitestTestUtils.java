package org.infinitest.util;

import static java.lang.Thread.*;
import static org.infinitest.util.FakeEnvironments.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.infinitest.testrunner.InProcessRunner;
import org.infinitest.testrunner.JUnit4Runner;

import com.google.common.base.Predicate;

public abstract class InfinitestTestUtils
{
    private static final String BACKUP_EXT = ".infinitest_bak";

    public static File getFileForClass(Class<?> clazz)
    {
        return getFileForClass(clazz.getName());
    }

    public static File getFileForClass(String className)
    {
        for (File file : fakeBuildPaths())
        {
            File fileForClass = getFileForClass(file, className);
            if (fileForClass.exists())
                return fileForClass;
        }
        throw new IllegalArgumentException(className + " does not exist");
    }

    public static File createBackup(String className) throws Exception
    {
        File originalFile = getFileForClass(className);
        File backupFile = new File(originalFile.getAbsolutePath() + BACKUP_EXT);
        InfinitestTestUtils.copyFile(originalFile, backupFile);
        return backupFile;
    }

    public static void restoreFromBackup(File backup)
    {
        File originalFile = new File(backup.getAbsolutePath().replace(BACKUP_EXT, ""));
        String fileName = originalFile.getAbsolutePath();
        originalFile.delete();
        if (!backup.renameTo(new File(fileName)))
            throw new IllegalStateException(originalFile + " could not be restored");
    }

    public static File getFileForClass(File baseDir, String classname)
    {
        return new File(baseDir + "/" + classname.replace(".", "/") + ".class");
    }

    public static void copyFile(File in, File out) throws Exception
    {
        if (!out.exists())
            out.createNewFile();

        if (!in.exists())
            throw new IllegalArgumentException(in + " does not exist");

        FileInputStream fis = new FileInputStream(in);
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

    public static Predicate<String> containsSubstring(final String substring)
    {
        return new Predicate<String>()
        {
            public boolean apply(String input)
            {
                return input.contains(substring);
            }
        };
    }

    public static List<String> emptyStringList()
    {
        return new ArrayList<String>();
    }

    public static boolean testIsBeingRunFromInfinitest()
    {
        StackTraceElement[] currentStack = currentThread().getStackTrace();
        List<String> classNames = InfinitestUtils.getClassNames(currentStack);
        return classNames.contains(InProcessRunner.class.getName())
                        || classNames.contains(JUnit4Runner.class.getName());
    }

    public static Throwable throwableWithStack(final StackTraceElement... stack)
    {
        return new Throwable()
        {
            @Override
            public StackTraceElement[] getStackTrace()
            {
                return stack;
            }
        };
    }
}
