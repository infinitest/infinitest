package org.infinitest.parser;

import static com.google.common.collect.Lists.*;
import static java.io.File.*;
import static org.apache.commons.lang.StringUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.infinitest.MissingClassException;

public class JavaAssistClassParser implements ClassParser
{
    private ClassPool classPool;
    private final String classpath;

    public JavaAssistClassParser(String classpath)
    {
        this.classpath = classpath;
    }

    private ClassPool getClassPool()
    {
        if (classPool == null)
        {
            classPool = new ClassPool();
            // This is used primarily for getting Java core objects like String and Integer,
            // so if we don't have the project's JDK classpath, it's probably OK.
            classPool.appendSystemPath();
            try
            {
                for (String pathElement : getPathElements())
                {
                    classPool.appendClassPath(pathElement);
                }
            }
            catch (NotFoundException e)
            {
                classPool = null; // RISK Untested
                // Blank out the class pool so we try again next time
                throw new MissingClassException("Could not create class pool", e);
            }
        }
        return classPool;
    }

    private Iterable<String> getPathElements()
    {
        List<String> entries = newArrayList(split(classpath, pathSeparator));
        ListIterator<String> iter = entries.listIterator();
        while (iter.hasNext())
        {
            if (entryDoesNotExist(iter))
            {
                iter.remove();
            }
        }
        return entries;
    }

    private boolean entryDoesNotExist(ListIterator<String> iter)
    {
        return !new File(iter.next()).exists();
    }

    public JavaClass getClass(String className)
    {
        CtClass cachedClass = getCachedClass(className);
        if (unparsableClass(cachedClass))
        {
            return new UnparsableClass(className);
        }
        JavaAssistClass javaClass = new JavaAssistClass(cachedClass);
        URL url = getClassPool().find(className);
        if (url != null && url.getProtocol().equals("file"))
        {
            javaClass.setClassFile(new File(url.getFile()));
        }
        return javaClass;
    }

    private boolean unparsableClass(CtClass cachedClass)
    {
        return cachedClass.getClassFile2() == null;
    }

    private CtClass getCachedClass(String className)
    {
        try
        {
            return getClassPool().get(className);
        }
        catch (NotFoundException e)
        {
            throw new MissingClassException("Expected to find " + className, e);
        }
    }

    public JavaClass parse(File file) throws IOException
    {
        FileInputStream inputStream = new FileInputStream(file);
        CtClass ctClass = getClassPool().makeClass(inputStream);
        JavaAssistClass clazz = new JavaAssistClass(ctClass);
        clazz.setClassFile(file);
        inputStream.close();
        return clazz;
    }

    public void clear()
    {
        classPool = null;
    }
}
