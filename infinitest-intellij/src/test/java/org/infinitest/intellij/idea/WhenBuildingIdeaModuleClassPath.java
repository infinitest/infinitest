package org.infinitest.intellij.idea;

import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.intellij.openapi.module.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;

public class WhenBuildingIdeaModuleClassPath {
	private static final String PATH_A = "a";
	private static final String PATH_B = "b";
	private static final String PATH_C = "c";
	private static final String PATH_D = "d";

	private final CompilerModuleExtension compilerModuleExtension = mock(CompilerModuleExtension.class);
	private final ModuleRootManager moduleRootManagerMock = mock(ModuleRootManager.class);
	private final Module module = mock(Module.class);
	private final IdeaModuleSettings ideaModuleSettingsSpy = spy(new IdeaModuleSettings(module));

	@Test
	public void shouldIncludeAllCompilationClassesToClasspathElementsList() {
		OrderEntry orderEntry1 = orderEntry();
		OrderEntry orderEntry2 = orderEntry();
		doReturn(new OrderEntry[] { orderEntry1, orderEntry2 }).when(moduleRootManagerMock).getOrderEntries();
		doReturn(new VirtualFile[] { fileWith(PATH_D) }).when(compilerModuleExtension).getOutputRoots(true);
		doReturn(new VirtualFile[] { fileWith(PATH_A), fileWith(PATH_B) }).when(orderEntry1).getFiles(OrderRootType.CLASSES);
		doReturn(new VirtualFile[] { fileWith(PATH_C) }).when(orderEntry2).getFiles(OrderRootType.CLASSES);
		doReturn(moduleRootManagerMock).when(ideaModuleSettingsSpy).moduleRootManagerInstance();
		doReturn(compilerModuleExtension).when(ideaModuleSettingsSpy).compilerModuleExtension();

		final List<File> classPathElementsList = ideaModuleSettingsSpy.listClasspathElements();

		assertThat(classPathElementsList.get(0).getPath(), equalTo(PATH_A));
		assertThat(classPathElementsList.get(1).getPath(), equalTo(PATH_B));
		assertThat(classPathElementsList.get(2).getPath(), equalTo(PATH_C));
		assertThat(classPathElementsList.get(3).getPath(), equalTo(PATH_D));
		assertThat(classPathElementsList.size(), equalTo(4));
	}

	private static OrderEntry orderEntry() {
		return mock(OrderEntry.class);
	}

	private static VirtualFile fileWith(final String path) {
		VirtualFile virtualFile = mock(VirtualFile.class);
		doReturn(path).when(virtualFile).getPath();
		return virtualFile;
	}
}
