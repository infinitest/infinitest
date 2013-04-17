/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.intellij.idea;

import static java.io.File.*;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.infinitest.*;
import org.infinitest.intellij.*;
import org.jetbrains.annotations.*;
import org.testng.collections.*;

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
	 *         modules
	 */
	List<File> listClasspathElements() {
		// Classpath order is significant
		List<File> classpathElements = Lists.newArrayList();

		// all our dependencies (recursively where needed)
		for (OrderEntry entry : moduleRootManagerInstance().getOrderEntries()) {
			List<VirtualFile> files = new ArrayList<VirtualFile>();

			if (entry instanceof ModuleOrderEntry) {
				/*
				 * other modules we depend on -> they could depend on modules
				 * themselves, so we need to add them recursively.
				 */
				Module currentModule = ((ModuleOrderEntry) entry).getModule();
				if (currentModule != null) {
					files.addAll(Arrays.asList(OrderEnumerator.orderEntries(currentModule).compileOnly().recursively().classes().getRoots()));
				}
			} else if (entry instanceof LibraryOrderEntry) {
				/*
				 * libraries cannot be recursive and we only need the classes of
				 * them.
				 */
				LibraryOrderEntry libraryOrderEntry = (LibraryOrderEntry) entry;
				files.addAll(Arrays.asList(libraryOrderEntry.getRootFiles(OrderRootType.CLASSES)));
			} else {
				/*
				 * all other cases (whichever they are) we want to have their
				 * classes outputs.
				 */
				files.addAll(Arrays.asList(entry.getFiles(OrderRootType.CLASSES)));
			}
			for (VirtualFile virtualFile : files) {
				classpathElements.add(new File(virtualFile.getPath()));
			}
		}

		CompilerModuleExtension compilerExtension = compilerModuleExtension();
		if (compilerExtension != null) {
			for (VirtualFile virtualFile : compilerExtension.getOutputRoots(true)) {
				classpathElements.add(new File(virtualFile.getPath()));
			}
		}

		return classpathElements;
	}

	ModuleRootManager moduleRootManagerInstance() {
		return ModuleRootManager.getInstance(module);
	}

	CompilerModuleExtension compilerModuleExtension() {
		return CompilerModuleExtension.getInstance(module);
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
