package org.infinitest.filter;

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 */
public class RegexFileFilter extends ClassNameFilter implements TestFilter
{
    private File file;

    public RegexFileFilter(File filterFile)
    {
        file = filterFile;
        if (!file.exists())
            log(INFO, "Filter file " + file + " does not exist.");

        updateFilterList();
    }

    public RegexFileFilter()
    {
        super();
    }

    public void updateFilterList()
    {
        if (file == null)
            return;

        clearFilters();
        if (file.exists())
            tryToReadFilterFile();
    }

    private void tryToReadFilterFile()
    {
        try
        {
            readFilterFile();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Something horrible happened to the filter file", e);
        }
    }

    private void readFilterFile() throws IOException
    {
        FileReader fileReader = new FileReader(file);
        try
        {
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            do
            {
                line = reader.readLine();
                addFilter(line);
            } while (line != null);
        }
        finally
        {
            fileReader.close();
        }
    }

    public void appendFilter(String regex) throws IOException
    {
        PrintWriter writer = new PrintWriter(new FileOutputStream(file, true));

        try
        {
            writer.println(regex);
        }
        finally
        {
            writer.close();
        }

        updateFilterList();
    }
}
