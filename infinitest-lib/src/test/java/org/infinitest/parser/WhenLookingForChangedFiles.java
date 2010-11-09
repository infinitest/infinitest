package org.infinitest.parser;

import static java.util.Collections.*;
import static org.easymock.EasyMock.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.infinitest.ClasspathProvider;
import org.infinitest.StandaloneClasspath;
import org.infinitest.changedetect.ChangeDetector;
import org.infinitest.changedetect.FileChangeDetector;
import org.infinitest.util.InfinitestTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fakeco.fakeproduct.FakeProduct;
import com.fakeco.fakeproduct.TestFakeProduct;

public class WhenLookingForChangedFiles
{
    private File altClassDir;
    private ChangeDetector detector;
    protected long timestamp;
    private ClasspathProvider classpath;

    @Before
    public void inContext() throws Exception
    {
        altClassDir = new File("altClasses");
        FileUtils.forceMkdir(altClassDir);
        List<File> buildPaths = new ArrayList<File>();
        buildPaths.addAll(fakeBuildPaths());
        buildPaths.add(altClassDir);
        classpath = new StandaloneClasspath(buildPaths);
        detector = new FileChangeDetector();
        detector.setClasspathProvider(classpath);
    }

    @After
    public void cleanup() throws Exception
    {
        FileUtils.deleteDirectory(altClassDir);
    }

    @Test
    public void shouldLookInClasspathForFiles()
    {
        ClasspathProvider mockClasspath = createMock(ClasspathProvider.class);
        expect(mockClasspath.classDirectoriesInClasspath()).andReturn(Collections.<File> emptyList());
        replay(mockClasspath);

        new FileChangeDetector().setClasspathProvider(mockClasspath);
        verify(mockClasspath);
    }

    @Test
    public void shouldHandleStrangeClasspaths() throws Exception
    {
        ChangeDetector changeDetector = new FileChangeDetector();
        changeDetector.setClasspathProvider(new StandaloneClasspath(new ArrayList<File>(), ""));
        assertTrue(changeDetector.findChangedFiles().isEmpty());
    }

    @Test
    public void shouldFindChangedFiles() throws IOException
    {
        Set<File> files = detector.findChangedFiles();
        assertTrue("Should have found changed files on first run", files.size() > 0);
        assertThat("TestFakeProduct should be in changed list", files, hasItem(getFileForClass(TestFakeProduct.class)));
        assertTrue("FakeProduct should be in changed list", files.contains(getFileForClass(FakeProduct.class)));
        assertTrue("Should have no changed files now", detector.findChangedFiles().isEmpty());
    }

    @Test
    public void canLookInMultipleClassDirectories() throws Exception
    {
        File newFile = createFileForClass(TestFakeProduct.class);
        File thisFile = InfinitestTestUtils.getFileForClass(getClass());
        Set<File> changedFiles = detector.findChangedFiles();
        assertThat(changedFiles, hasItem(newFile));
        assertThat(changedFiles, hasItem(thisFile));
    }

    @Test
    public void shouldFindRemovedFiles() throws Exception
    {
        File newFile = createFileForClass(TestFakeProduct.class);
        assertThat(detector.findChangedFiles(), hasItem(newFile));

        FileUtils.forceDelete(newFile);

        assertTrue(detector.filesWereRemoved());
        assertThat(detector.findChangedFiles(), not(hasItem(newFile)));
    }

    @Test
    public void shouldDetectChangedFilesByTimeStamp() throws Exception
    {
        detector = new FileChangeDetector()
        {
            @Override
            protected long getModificationTimestamp(File classFile)
            {
                return timestamp;
            }
        };
        detector.setClasspathProvider(classpath);
        assertFalse("Should have found changed files on first run", detector.findChangedFiles().isEmpty());
        assertTrue("Timestamp is unchanged", detector.findChangedFiles().isEmpty());
        timestamp += 100;
        assertFalse("Timestamp changed", detector.findChangedFiles().isEmpty());
    }

    @Test
    public void shouldBeTolerantOfDissapearingDirectories() throws Exception
    {
        detector = new FileChangeDetector()
        {
            @Override
            protected File[] childrenOf(File directory)
            {
                return null;
            }
        };
        detector.setClasspathProvider(classpath);
        assertEquals(emptySet(), detector.findChangedFiles());
    }

    private File createFileForClass(Class<TestFakeProduct> clazz) throws IOException
    {
        File destFile = InfinitestTestUtils.getFileForClass(altClassDir, clazz.getName());
        assertTrue(destFile.getParentFile().mkdirs());
        assertTrue(destFile.createNewFile());
        return destFile;
    }
}
