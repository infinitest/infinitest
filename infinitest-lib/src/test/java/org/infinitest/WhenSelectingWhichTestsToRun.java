package org.infinitest;

import static java.util.Arrays.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.infinitest.filter.FilterStub;
import org.infinitest.parser.ClassFileTestDetector;
import org.junit.Test;

import com.fakeco.fakeproduct.TestFakeProduct;

public class WhenSelectingWhichTestsToRun
{
    @Test
    public void shouldIgnoreTestsInDependentProjects()
    {
        List<File> outputDirs = asList(new File("target/classes"));
        List<File> classDirsInClasspath = fakeBuildPaths();
        String rawClasspath = fakeClasspath().getCompleteClasspath();
        StandaloneClasspath classpath = new StandaloneClasspath(outputDirs, classDirsInClasspath, rawClasspath);

        ClassFileTestDetector testDetector = new ClassFileTestDetector(new FilterStub());
        testDetector.setClasspathProvider(classpath);
        File classFileNotInOutputDirectory = getFileForClass(TestFakeProduct.class);
        assertTrue(testDetector.findTestsToRun(asList(classFileNotInOutputDirectory)).isEmpty());
    }
}
