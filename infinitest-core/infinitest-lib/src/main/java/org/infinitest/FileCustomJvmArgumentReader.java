package org.infinitest;

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class FileCustomJvmArgumentReader implements CustomJvmArgumentsReader
{
    public static final String FILE_NAME = "infinitest.args";

    private final File parentDirectory;

    public FileCustomJvmArgumentReader(File parentDirectory)
    {
        this.parentDirectory = parentDirectory;
    }

    public List<String> readCustomArguments()
    {
        File file = new File(parentDirectory, FILE_NAME);
        if (!file.exists())
        {
            return emptyList();
        }

        try
        {
            @SuppressWarnings("unchecked")
            List<String> lines = IOUtils.readLines(new FileInputStream(file));
            if (lines.isEmpty())
            {
                return emptyList();
            }

            return buildArgumentList(lines);

        }
        catch (IOException e)
        {
            return emptyList();
        }
    }

    private List<String> buildArgumentList(List<String> lines)
    {
        List<String> arguments = newArrayList();
        for (String line : lines)
        {
            for (String arg : line.split(" "))
            {
                arguments.add(arg);
            }
        }

        return arguments;
    }
}
