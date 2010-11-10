package org.infinitest.parser;

import static com.google.common.collect.Sets.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.infinitest.ClasspathProvider;
import org.infinitest.filter.TestFilter;

/**
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 */
public class ClassFileTestDetector implements TestDetector
{
    private final TestFilter filters;
    private ClassFileIndex index;
    private ClasspathProvider classpath;

    public ClassFileTestDetector(TestFilter testFilterList)
    {
        filters = testFilterList;
    }

    public void clear()
    {
        index.clear();
    }

    /**
     * Runs through the classpath looking for changed files and returns the set of tests that need
     * to be run.
     */
    public synchronized Set<JavaClass> findTestsToRun(Collection<File> changedFiles)
    {
        filters.updateFilterList();

        // Find changed classes
        Set<JavaClass> changedClasses = index.findClasses(changedFiles);

        // Loop through all changed classes, adding their parents (and their parents)
        // to another set of changed classes
        Set<JavaClass> changedParents = new HashSet<JavaClass>();
        for (JavaClass jclass : changedClasses)
        {
            index.findParents(changedClasses, changedParents, jclass);
        }

        // combine two sets
        changedClasses.addAll(changedParents);

        // run through total set, and pick out tests to run
        log(Level.FINE, "Total changeset: " + changedParents);
        return filterTests(changedClasses);
    }

    private Set<JavaClass> filterTests(Set<JavaClass> changedClasses)
    {
        Set<JavaClass> testsToRun = new HashSet<JavaClass>();
        for (JavaClass jclass : changedClasses)
        {
            if (isATest(jclass) && inCurrentProject(jclass))
            {
                testsToRun.add(jclass);
            }
            else
            {
                log(Level.FINE, "Filtered test: " + jclass);
            }
        }
        return testsToRun;
    }

    private boolean inCurrentProject(JavaClass jclass)
    {
        // I can't find a scenario where a non-classfile could get in here, but I think I'm missing
        // it, so I still want to guard against it
        if (jclass.locatedInClassFile())
        {
            File classFile = jclass.getClassFile();
            for (File parentDir : classpath.getClassOutputDirs())
            {
                if (classFile.getAbsolutePath().startsWith(parentDir.getAbsolutePath()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isATest(JavaClass jclass)
    {
        return jclass.isATest() && !filters.match(jclass.getName());
    }

    boolean isIndexed(Class<Object> clazz)
    {
        return index.isIndexed(clazz);
    }

    public boolean isEmpty()
    {
        return index.isEmpty();
    }

    public Set<String> getIndexedClasses()
    {
        return index.getIndexedClasses();
    }

    public JavaClass findJavaClass(String name)
    {
        return index.findJavaClass(name);
    }

    public void setClasspathProvider(ClasspathProvider classpath)
    {
        this.classpath = classpath;
        index = new ClassFileIndex(classpath);
    }

    public Set<String> getCurrentTests()
    {
        Set<String> tests = newHashSet();
        for (String each : getIndexedClasses())
        {
            if (isATest(index.findJavaClass(each)))
            {
                tests.add(each);
            }
        }
        return tests;
    }
}
