package org.infinitest.parser;

import java.io.File;
import java.io.IOException;

import javassist.NotFoundException;

import org.infinitest.ClasspathProvider;
import org.infinitest.MissingClassException;

/**
 * @author Ben Rady
 */
class JavaClassBuilder implements ClassBuilder
{
    private final ClassParser parser;

    JavaClassBuilder(ClasspathProvider classpath)
    {
        this(new JavaAssistClassParser(classpath.getCompleteClasspath()));
    }

    public JavaClassBuilder(ClassParser parser)
    {
        this.parser = parser;
    }

    public JavaClass createClass(String classname)
    {
        try
        {
            return parser.getClass(classname);
        }
        // CHECKSTYLE:OFF
        catch (RuntimeException e)
        // CHECKSTYLE:ON
        {
            // Can occur when a cached class disappears from the file system
            rethrowIfSerious(e);
            return new UnparsableClass(classname);
        }
        catch (MissingClassException e)
        {
            return new UnparsableClass(classname);
        }
    }

    public JavaClass loadClass(File file)
    {
        try
        {
            return parser.parse(file);
        }
        // CHECKSTYLE:OFF
        catch (RuntimeException e)
        // CHECKSTYLE:ON
        {
            // If the class goes missing after we read it in but before we process it,
            // we might get an exception that looks like this
            rethrowIfSerious(e);
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    private void rethrowIfSerious(RuntimeException e)
    {
        if (!(e.getCause() instanceof NotFoundException))
            throw e;
    }

    public void clear()
    {
        parser.clear();
    }
}
