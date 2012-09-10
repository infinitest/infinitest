package org.infinitest.intellij.idea;


import com.intellij.openapi.module.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;

import java.io.*;
import java.util.*;

import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WhenBuildingIdeaModuleClassPath {

    public static final String PATH_A = "a";
    public static final String PATH_B = "b";
    public static final String PATH_C = "c";
    public static final String PATH_D = "d";
    public static final String PATH_E = "e";

    @Mock
    private ModuleRootManager moduleRootManagerMock;

    @Mock
    private Module module;

    @Spy
    private IdeaModuleSettings ideaModuleSettingsSpy = new IdeaModuleSettings(module);

    @Test
    public void shouldIncludeAllCompilationClassesToClasspathElementsList() {
        final OrderEntry orderEntryMock1 = orderEntryMock();
        final OrderEntry orderEntryMock2 = orderEntryMock();
        doReturn(new VirtualFile[]{mockedVirtualFileWith(PATH_A), mockedVirtualFileWith(PATH_B)}).when(orderEntryMock1).getFiles(OrderRootType.COMPILATION_CLASSES);
        doReturn(new VirtualFile[]{mockedVirtualFileWith(PATH_C)}).when(orderEntryMock1).getFiles(OrderRootType.CLASSES_AND_OUTPUT);
        doReturn(new VirtualFile[]{mockedVirtualFileWith(PATH_D)}).when(orderEntryMock2).getFiles(OrderRootType.COMPILATION_CLASSES);
        doReturn(new VirtualFile[]{mockedVirtualFileWith(PATH_E)}).when(orderEntryMock2).getFiles(OrderRootType.CLASSES_AND_OUTPUT);
        doReturn(new OrderEntry[] {orderEntryMock1, orderEntryMock2}).when(moduleRootManagerMock).getOrderEntries();
        doReturn(moduleRootManagerMock).when(ideaModuleSettingsSpy).moduleRootManagerInstance();

        final List<File> classPathElementsList = ideaModuleSettingsSpy.listClasspathElements();

        verify(ideaModuleSettingsSpy, times(1)).moduleRootManagerInstance();
        verify(moduleRootManagerMock, times(1)).getOrderEntries();
        verify(orderEntryMock1, times(1)).getFiles(OrderRootType.COMPILATION_CLASSES);
        verify(orderEntryMock2, times(1)).getFiles(OrderRootType.COMPILATION_CLASSES);
        assertThat(classPathElementsList.get(0).getPath(), equalTo(PATH_A));
        assertThat(classPathElementsList.get(1).getPath(), equalTo(PATH_B));
        assertThat(classPathElementsList.get(2).getPath(), equalTo(PATH_D));
        assertThat(classPathElementsList.size(), equalTo(3));
    }

    private OrderEntry orderEntryMock() {
        return mock(OrderEntry.class);
    }

    private VirtualFile mockedVirtualFileWith(final String path) {
        final VirtualFile virtualFile = mock(VirtualFile.class);
        doReturn(path).when(virtualFile).getPath();

        return virtualFile;
    }
}
