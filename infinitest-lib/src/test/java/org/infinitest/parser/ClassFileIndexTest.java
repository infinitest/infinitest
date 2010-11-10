package org.infinitest.parser;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.*;
import static org.easymock.EasyMock.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.infinitest.changedetect.FileChangeDetector;
import org.junit.Before;
import org.junit.Test;

import com.fakeco.fakeproduct.FakeProduct;

public class ClassFileIndexTest
{
    private ClassFileIndex index;
    private ClassBuilder builder;

    @Before
    public void inContext()
    {
        index = new ClassFileIndex(fakeClasspath());
        builder = createMock(ClassBuilder.class);
        index.setBuilder(builder);
    }

    @Test
    public void shouldClearClassBuilderAfterLookingForJavaFilesToReduceMemoryFootprint()
    {
        expect(builder.loadClass((File) anyObject())).andReturn(new FakeJavaClass(""));
        builder.clear();

        replay(builder);

        index.findClasses(asList(getFileForClass(FakeProduct.class)));
        verify(builder);
    }

    @Test
    public void shouldReplaceEntriesInTheIndex()
    {
        expect(builder.loadClass((File) anyObject())).andReturn(new FakeJavaClass("FakeProduct"));
        JavaClass secondClass = new FakeJavaClass("FakeProduct");
        expect(builder.loadClass((File) anyObject())).andReturn(secondClass);
        builder.clear();
        expectLastCall().times(2);
        replay(builder);

        index.findClasses(asList(getFileForClass(FakeProduct.class)));
        index.findClasses(asList(getFileForClass(FakeProduct.class)));
        assertSame(secondClass, index.findJavaClass("FakeProduct"));
    }

    @Test
    public void shouldDisposeOfJavaClassesAfterAddingToIndex()
    {
        JavaClass mockClass = createMock(JavaClass.class);
        expect(mockClass.locatedInClassFile()).andReturn(true);
        expect(mockClass.getImports()).andReturn(Collections.<String> emptyList());
        mockClass.dispose();
        expect(builder.createClass("FakeClass")).andReturn(mockClass);
        replay(builder, mockClass);

        index.findJavaClass("FakeClass");
        verify(builder, mockClass);
    }

    @Test
    public void shouldIgnoreClassFilesThatCannotBeParsed()
    {
        ClassFileIndex index = new ClassFileIndex(fakeClasspath());
        assertEquals(Collections.emptySet(), index.findClasses(newArrayList(new File("notAClassFile"))));
    }

    public static void main(String[] args) throws IOException
    {
        FileChangeDetector detector = new FileChangeDetector();
        detector.setClasspathProvider(fakeClasspath());
        ArrayList<File> files = newArrayList(detector.findChangedFiles());
        List<ClassFileIndex> indexes = newArrayList();
        int totalClasses = 0;
        long start = System.currentTimeMillis();
        while (true)
        {
            ClassFileIndex index = new ClassFileIndex(fakeClasspath());
            index.findClasses(files);
            indexes.add(index);
            totalClasses += index.getIndexedClasses().size();
            System.out.println(totalClasses + "\t" + (System.currentTimeMillis() - start));
        }
    }
}
