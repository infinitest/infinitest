package org.infinitest.intellij.idea;

import static java.io.File.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.intellij.InfinitestJarLocator;
import org.infinitest.intellij.ModuleSettings;
import org.jetbrains.annotations.Nullable;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;

public class IdeaModuleSettings implements ModuleSettings
{
    private final Module module;
    private final InfinitestJarLocator locator = new InfinitestJarLocator();

    public IdeaModuleSettings(Module module)
    {
        this.module = module;
    }

    public void writeToLogger(Logger log)
    {
        java.util.List<File> outputDirectories = listOutputDirectories();
        log.info("Output Directories:");
        for (File each : outputDirectories)
        {
            log.info(each.getAbsolutePath());
        }

        String classpathString = buildClasspathString();
        log.info("Classpath:");
        log.info(classpathString);
    }

    public String getName()
    {
        return module.getName();
    }

    @Nullable
    public RuntimeEnvironment getRuntimeEnvironment()
    {
        File sdkPath = getSdkHomePath();
        if (sdkPath == null)
        {
            return null;
        }
        return new RuntimeEnvironment(listOutputDirectories(), getWorkingDirectory(), buildClasspathString(), new File(
                        sdkPath.getAbsolutePath()));
    }

    /**
     * List all output directories for the project including both production and test
     * 
     * @return A list of all of the output directories for the project
     */
    private List<File> listOutputDirectories()
    {
        List<File> outputDirectories = new ArrayList<File>();

        outputDirectories.add(new File(CompilerPaths.getModuleOutputPath(module, false)));
        outputDirectories.add(new File(CompilerPaths.getModuleOutputPath(module, true)));

        return outputDirectories;
    }

    /**
     * Creates a classpath string consisting of all libraries and output directories
     * 
     * @return A string representation of the classpath entries deliniated by colons
     */
    private String buildClasspathString()
    {
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (File each : listClasspathElements())
        {
            if (!first)
            {
                builder.append(pathSeparator);
            }
            first = false;
            builder.append(each.getAbsolutePath().replace("!", ""));
        }

        return appendInfinitestJarTo(builder.toString());
    }

    private File getWorkingDirectory()
    {
        return new File(module.getModuleFilePath()).getParentFile();
    }

    /**
     * Lists all classpath elements including output directories and libraries
     * 
     * @return Collection unique classpath elements across all of the project's modeuls
     */
    private List<File> listClasspathElements()
    {
        // Classpath order is significant
        List<File> classpathElements = new ArrayList<File>();

        for (OrderEntry entry : ModuleRootManager.getInstance(module).getOrderEntries())
        {
            for (VirtualFile virtualFile : entry.getFiles(OrderRootType.CLASSES_AND_OUTPUT))
            {
                classpathElements.add(new File(virtualFile.getPath()));
            }
        }

        return classpathElements;
    }

    private String appendInfinitestJarTo(String classpath)
    {
        StringBuilder builder = new StringBuilder(classpath);
        for (String each : infinitestJarPaths())
        {
            builder.append(System.getProperty("path.separator"));
            builder.append(each);
        }

        return builder.toString();
    }

    private List<String> infinitestJarPaths()
    {
        PluginId pluginId = PluginManager.getPluginByClassName(getClass().getName());
        IdeaPluginDescriptor descriptor = PluginManager.getPlugin(pluginId);
        File pluginPath = descriptor.getPath();

        List<String> paths = new ArrayList<String>();
        for (String each : locator.findInfinitestJarNames())
        {
            paths.add(new File(pluginPath, "lib/" + each).getAbsolutePath());
        }

        return paths;
    }

    @Nullable
    private File getSdkHomePath()
    {
        for (Sdk each : ProjectJdkTable.getInstance().getAllJdks())
        {
            if (!"IDEA JDK".equals(each.getSdkType().getName()))
            {
                return new File(each.getHomePath());
            }
        }
        return null;
    }
}
