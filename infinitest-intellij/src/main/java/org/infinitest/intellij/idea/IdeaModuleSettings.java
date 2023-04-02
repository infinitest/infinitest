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
import static org.infinitest.config.FileBasedInfinitestConfigurationSource.INFINITEST_FILTERS_FILE_NAME;
import static org.infinitest.util.InfinitestUtils.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.intellij.InfinitestJarsLocator;
import org.infinitest.intellij.ModuleSettings;
import org.jetbrains.annotations.Nullable;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.util.ProgramParametersConfigurator;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.vfs.VirtualFile;

public class IdeaModuleSettings implements ModuleSettings {
	private final Module module;
	private final InfinitestJarsLocator locator = new InfinitestJarsLocator();

	private static final Boolean TEST_CLASSES = true;
	private static final Boolean MAIN_CLASSES = false;

	/**
	 * @param module Injected by the platform
	 */
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
		
		File workingDirectory = getWorkingDirectory();
		
		IdeaInfinitestConfigurationSource configurationSource = new IdeaInfinitestConfigurationSource(module);
		IdeaCustomJvmArgumentsReader argumentsReader = new IdeaCustomJvmArgumentsReader(module);
		
		return new RuntimeEnvironment(new File(sdkPath.getAbsolutePath()),
				workingDirectory,
				runnerClassLoaderClassPath,
				runnerProcessClassPath,
				listOutputDirectories(),
				buildClasspathString(),
				configurationSource,
				argumentsReader);
	}

	/**
	 * List all output directories for the project including both production and
	 * test
	 * 
	 * @return A list of all of the output directories for the project
	 */
	protected List<File> listOutputDirectories() {
		List<File> outputDirectories = new ArrayList<>();

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
			File file = new File(path);

			try {
				log(java.util.logging.Level.FINE, "Adding output directory: " + path);
				File canonicalFile = file.getCanonicalFile();
				outputDirectories.add(canonicalFile);
			} catch (IOException e) {
				log("Error while getting canonical file for: " + path, e);
				outputDirectories.add(file);
			}
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

	protected File getWorkingDirectory() {
		CommonProgramRunConfigurationParameters configuration = new InfinitestRunConfigurationParameters();
		configuration.setWorkingDirectory("%MODULE_WORKING_DIR%");
		
		ProgramParametersConfigurator configurator = new ProgramParametersConfigurator();
		String workingDir = configurator.getWorkingDir(configuration, module.getProject(), module);
		
		return new File(workingDir);
	}
	
	/**
	 * @param workingDirectory
	 * @return The filter file for the module, or if it does not exist the project filter file, or null if it does not exist
	 */
	@Override
	public File getFilterFile() {
		for (File rootDirectory : getRootDirectories()) {
			File filterFile = new File(rootDirectory, INFINITEST_FILTERS_FILE_NAME);
			
			if (filterFile.exists()) {
				return filterFile;
			}
		}
		
		return null;
	}
	
	@Override
	public List<File> getRootDirectories() {
		List<File> directories = new ArrayList<>();
		
		directories.add(getWorkingDirectory());

		String basePath = module.getProject().getBasePath();
		if (basePath != null) {
			directories.add(new File(basePath));
		}
		
		return directories;
	}

	/**
	 * Lists all classpath elements including output directories and libraries
	 * 
	 * @return Collection unique classpath elements across all of the project's
	 *         modules
	 */
	List<File> listClasspathElements() {
		// Classpath order is significant
		List<File> classpathElements = new ArrayList<>();
		
		VirtualFile[] files = OrderEnumerator.orderEntries(module)
				.withoutSdk()
				.recursively()
				.classes()
				.getRoots();
		
		for (VirtualFile virtualFile : files) {
			classpathElements.add(new File(virtualFile.getPath()));
		}

		return classpathElements;
	}

	protected String infinitestJarPath(String jarName) {
		PluginId pluginId = PluginManager.getPluginByClassName(getClass().getName());
		IdeaPluginDescriptor descriptor = PluginManager.getPlugin(pluginId);
		Path pluginPath = descriptor.getPluginPath();

		Path jar = pluginPath.resolve("lib/" + jarName);
		return jar.toString();		
	}
	
	@Nullable
	protected File getSdkHomePath() {
		Sdk moduleSdk = ModuleRootManager.getInstance(module).getSdk();
		if (moduleSdk == null) {
			return null;
		}

		return new File(moduleSdk.getHomePath());
	}
}
