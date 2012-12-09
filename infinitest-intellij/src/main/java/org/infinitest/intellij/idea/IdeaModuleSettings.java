/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.intellij.idea;

import static java.io.File.*;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.infinitest.*;
import org.infinitest.intellij.*;
import org.jetbrains.annotations.*;

import com.intellij.ide.plugins.*;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.extensions.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;

public class IdeaModuleSettings implements ModuleSettings {
	private final Module module;
	private final InfinitestJarLocator locator = new InfinitestJarLocator();

	public IdeaModuleSettings(Module module) {
		this.module = module;
	}

	public void writeToLogger(Logger log) {
		java.util.List<File> outputDirectories = listOutputDirectories();
		log.info("Output Directories:");
		for (File each : outputDirectories) {
			log.info(each.getAbsolutePath());
		}

		String classpathString = buildClasspathString();
		log.info("Classpath:");
		log.info(classpathString);
	}

	public String getName() {
		return module.getName();
	}

	@Nullable
	public RuntimeEnvironment getRuntimeEnvironment() {
		File sdkPath = getSdkHomePath();
		if (sdkPath == null) {
			return null;
		}
		return new RuntimeEnvironment(listOutputDirectories(), getWorkingDirectory(), buildClasspathString(), new File(sdkPath.getAbsolutePath()));
	}

	/**
	 * List all output directories for the project including both production and
	 * test
	 * 
	 * @return A list of all of the output directories for the project
	 */
	private List<File> listOutputDirectories() {
		List<File> outputDirectories = new ArrayList<File>();

		outputDirectories.add(new File(CompilerPaths.getModuleOutputPath(module, false)));
		outputDirectories.add(new File(CompilerPaths.getModuleOutputPath(module, true)));

		return outputDirectories;
	}

	/**
	 * Creates a classpath string consisting of all libraries and output
	 * directories
	 * 
	 * @return A string representation of the classpath entries deliniated by
	 *         colons
	 */
	private String buildClasspathString() {
		StringBuilder builder = new StringBuilder();

		boolean first = true;
		for (File each : listClasspathElements()) {
			if (!first) {
				builder.append(pathSeparator);
			}
			first = false;
			builder.append(each.getAbsolutePath().replace("!", ""));
		}

		return appendInfinitestJarTo(builder.toString());
	}

	private File getWorkingDirectory() {
		return new File(module.getModuleFilePath()).getParentFile();
	}

	/**
	 * Lists all classpath elements including output directories and libraries
	 * 
	 * @return Collection unique classpath elements across all of the project's
	 *         modeuls
	 */
	List<File> listClasspathElements() {
		// Classpath order is significant
		Set<File> classpathElements = new LinkedHashSet<File>();


		if(isLegacyApiAvailable()) {
			classpathElements.addAll(findClasspathElementsLegacyMode());
		} else {
			classpathElements.addAll(findClassPathElements());
		}
		return new ArrayList<File>(classpathElements);
	}

	/**
	 * In order to maximize backwards compatilibity, we check if the environment supports the "old way" of
	 * finding the modules classpath.
	 */
	private boolean isLegacyApiAvailable() {
		try {
			OrderRootType.class.getField("COMPILATION_CLASSES");
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}
	/**
	 * This is the new way of doing it. Starting with IDEA-12 the previously deprecated
	 * {@code OrderRootType.COMPILATION_CLASSES} has been removed entirely and can no longer be used.
	 * Instead we use the more verbose but more concise way of reading the classpath of all modules involved.
	 */
	private Collection<? extends File> findClassPathElements() {
		final List<File> found = new ArrayList<File>();

		/* our module output */
		CompilerModuleExtension compilerModuleExtension = CompilerModuleExtension.getInstance(module);
		if(compilerModuleExtension != null) {
			VirtualFile[] outputRoots = compilerModuleExtension.getOutputRoots(true);
			for (VirtualFile outputRoot : outputRoots) {
				found.add(new File(outputRoot.getPath()));
			}
		}

		/* all our dependencies (recursively where needed) */
		for (OrderEntry entry : moduleRootManagerInstance().getOrderEntries()) {
			List<VirtualFile> files = new ArrayList<VirtualFile>();

			if(entry instanceof ModuleOrderEntry) {
				/* other modules we depend on -> they could depend on modules themselves, so we need to add them recursively */
				ModuleOrderEntry moduleOrderEntry = (ModuleOrderEntry) entry;
				Module currentModule = moduleOrderEntry.getModule();
				if(currentModule != null) {
					files.addAll(Arrays.asList(OrderEnumerator.orderEntries(currentModule).compileOnly().recursively().classes().getRoots()));
				}
			} else if (entry instanceof LibraryOrderEntry) {
				/* libraries cannot be recursive and we only need the classes of them */
				LibraryOrderEntry libraryOrderEntry = (LibraryOrderEntry) entry;
				files.addAll(Arrays.asList(libraryOrderEntry.getRootFiles(OrderRootType.CLASSES)));
			} else {
				/* all other cases (whichever they are) we want to have their classes outputs */
				files.addAll(Arrays.asList(entry.getFiles(OrderRootType.CLASSES)));
			}
			for (VirtualFile virtualFile : files) {
				found.add(new File(virtualFile.getPath()));
			}
		}

		return found;
	}

	/**
	 * This is the "old" way of doing it. (Pre-IDEA-12)
	 * The {@code OrderRootType.COMPILATION_CLASSES} has been deprecated.
	 * {@link OrderRootType.COMPILATION_CLASSES}
	 * Javadoc states to use the following approaches instead:
	 * for libraries and jdk use CLASSES
	 * to get module output roots use CompilerModuleExtension.getOutputRoots(boolean)(true)
	 * to recursively process module dependencies use OrderEnumerator.orderEntries(module).recursively().exportedOnly()
	 * -> we do this in {@code findClassPathElements()}
	 */
	private Collection<? extends File> findClasspathElementsLegacyMode() {
		List<File> found = new ArrayList<File>();

		for (OrderEntry entry : moduleRootManagerInstance().getOrderEntries()) {
			for (VirtualFile virtualFile : entry.getFiles(OrderRootType.COMPILATION_CLASSES)) {
				found.add(new File(virtualFile.getPath()));
			}
		}

		return found;
	}

	ModuleRootManager moduleRootManagerInstance() {
        return ModuleRootManager.getInstance(module);
	}

	private String appendInfinitestJarTo(String classpath) {
		StringBuilder builder = new StringBuilder(classpath);
		for (String each : infinitestJarPaths()) {
			builder.append(System.getProperty("path.separator"));
			builder.append(each);
		}

		return builder.toString();
	}

	private List<String> infinitestJarPaths() {
		PluginId pluginId = PluginManager.getPluginByClassName(getClass().getName());
		IdeaPluginDescriptor descriptor = PluginManager.getPlugin(pluginId);
		File pluginPath = descriptor.getPath();

		List<String> paths = new ArrayList<String>();
		for (String each : locator.findInfinitestJarNames()) {
			paths.add(new File(pluginPath, "lib/" + each).getAbsolutePath());
		}

		return paths;
	}

	@Nullable
	private File getSdkHomePath() {
		Sdk projectJdk = ProjectRootManager.getInstance(module.getProject()).getProjectJdk();
		if (projectJdk == null) {
			return null;
		}

		return new File(projectJdk.getHomePath());
	}
}
