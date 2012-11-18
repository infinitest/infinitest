package org.infinitest.intellij.idea;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;

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

	@Mock
	private Module dependedModuleMock;

	@Mock
	private CompilerModuleExtension compilerModuleExtensionMock;

	@Spy
	private IdeaModuleSettings ideaModuleSettingsSpy = new IdeaModuleSettings(module);

	@Test
	public void shouldIncludeAllCompilationClassesToClasspathElementsList() {
		final OrderEntry orderEntryMock1 = orderEntryMock();
		final OrderEntry orderEntryMock2 = orderEntryMock();
		doReturn(new VirtualFile[] {mockedVirtualFileWith(PATH_A), mockedVirtualFileWith(PATH_B)}).when(orderEntryMock1)
				.getFiles(OrderRootType.CLASSES);
		doReturn(new VirtualFile[] {mockedVirtualFileWith(PATH_D)}).when(orderEntryMock2)
				.getFiles(OrderRootType.CLASSES);

		doReturn(new OrderEntry[] {orderEntryMock1, orderEntryMock2}).when(moduleRootManagerMock).getOrderEntries();
		doReturn(new Module[] {dependedModuleMock}).when(moduleRootManagerMock).getModuleDependencies(false);

		doReturn(new VirtualFile[] {mockedVirtualFileWith(PATH_C)}).when(compilerModuleExtensionMock)
				.getOutputRoots(false);
		doReturn(new VirtualFile[] {mockedVirtualFileWith(PATH_E)}).when(compilerModuleExtensionMock)
				.getOutputRoots(true);

		doReturn(moduleRootManagerMock).when(ideaModuleSettingsSpy).moduleRootManagerInstance();
		doReturn(compilerModuleExtensionMock).when(ideaModuleSettingsSpy)
				.compilerModuleExtensionInstance(any(Module.class));

		final List<File> classPathElementsList = ideaModuleSettingsSpy.listClasspathElements();

		verify(ideaModuleSettingsSpy, times(2)).moduleRootManagerInstance();
		verify(ideaModuleSettingsSpy, times(2)).compilerModuleExtensionInstance(any(Module.class));

		verify(moduleRootManagerMock, times(1)).getOrderEntries();
		verify(moduleRootManagerMock, times(1)).getModuleDependencies(false);

		verify(orderEntryMock1, times(1)).getFiles(OrderRootType.CLASSES);
		verify(orderEntryMock2, times(1)).getFiles(OrderRootType.CLASSES);
		assertThat(classPathElementsList.get(0).getPath(), equalTo(PATH_A));
		assertThat(classPathElementsList.get(1).getPath(), equalTo(PATH_B));
		assertThat(classPathElementsList.get(2).getPath(), equalTo(PATH_D));
		assertThat(classPathElementsList.get(3).getPath(), equalTo(PATH_C));
		assertThat(classPathElementsList.get(4).getPath(), equalTo(PATH_E));
		assertThat(classPathElementsList.size(), equalTo(5));
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
