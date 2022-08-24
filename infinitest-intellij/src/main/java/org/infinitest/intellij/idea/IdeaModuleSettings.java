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

import static java.io.File.pathSeparator;
import static org.infinitest.util.InfinitestUtils.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.intellij.InfinitestJarsLocator;
import org.infinitest.intellij.ModuleSettings;
import org.jetbrains.annotations.Nullable;
import org.testng.collections.Lists;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.JdkOrderEntry;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

public class IdeaModuleSettings implements ModuleSettings {
	private final Module module;
	private final InfinitestJarsLocator locator = new InfinitestJarsLocator();

	private static final Boolean TEST_CLASSES = true;
	private static final Boolean MAIN_CLASSES = false;

	public IdeaModuleSettings(Module module) {
		this.module = module;
	}

	@Override
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

	@Override
	public String getName() {
		return module.getName();
	}

	@Override
	@Nullable
	public RuntimeEnvironment getRuntimeEnvironment() {
		File sdkPath = getSdkHomePath();
		if (sdkPath == null) {
			return null;
		}
		String runnerClassLoaderClassPath = infinitestJarPath(locator.findInfinitestClassLoaderlJarName());
		String runnerProcessClassPath  = infinitestJarPath(locator.findInfinitestRunnerJarName());
		return new RuntimeEnvironment(new File(sdkPath.getAbsolutePath()), getWorkingDirectory(), runnerClassLoaderClassPath, runnerProcessClassPath, listOutputDirectories(), buildClasspathString());
	}

	/**
	 * List all output directories for the project including both production and
	 * test
	 * 
	 * @return A list of all of the output directories for the project
	 */
    private List<File> listOutputDirectories() {
        List<File> outputDirectories = new ArrayList<File>();

        addOutputDirectory(outputDirectories, CompilerPaths.getModuleOutputPath(module, TEST_CLASSES));
        addOutputDirectory(outputDirectories, CompilerPaths.getModuleOutputPath(module, MAIN_CLASSES));
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        for (Module dependantModule : moduleRootManager.getDependencies()) {
            addOutputDirectory(outputDirectories, CompilerPaths.getModuleOutputPath(dependantModule, MAIN_CLASSES));
        }

        return outputDirectories;
    }

    private void addOutputDirectory(List<File> outputDirectories, String path) {
        if (path != null) {
            log(java.util.logging.Level.FINE, "Adding output directory: " + path);
            outputDirectories.add(new File(path));
        }
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

		return builder.toString();
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
			} else if (!(entry instanceof JdkOrderEntry)) {
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

	private String infinitestJarPath(String jarName) {
		PluginId pluginId = PluginManager.getPluginByClassName(getClass().getName());
		IdeaPluginDescriptor descriptor = PluginManager.getPlugin(pluginId);
		File pluginPath = descriptor.getPath();

		File jar = new File(pluginPath, "lib/" + jarName);
		return jar.getAbsolutePath();		
	}
	
	@Nullable
	private File getSdkHomePath() {
		Sdk projectSdk = ProjectRootManager.getInstance(module.getProject()).getProjectSdk();
		if (projectSdk == null) {
			return null;
		}

		return new File(projectSdk.getHomePath());
	}
}
