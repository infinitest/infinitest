package org.infinitest.maven;

import static org.apache.commons.io.FileUtils.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ProcessLibDirectoryMojoTest
{
    @Test
    public void shouldAppendBundleClassPathToExistingBndFile() throws Exception
    {
        ProcessLibDirectoryMojo mojo = new ProcessLibDirectoryMojo();
        mojo.targetDirectory = new File("target");
        mojo.baseDirectory = new File("src/test/resources");

        mojo.execute();

        assertTrue(contentEquals(new File(mojo.baseDirectory + "/expected-osgi.bnd"), new File(mojo.targetDirectory
                        + "/osgi.bnd")));
        assertTrue(contentEquals(new File(mojo.baseDirectory + "/expected-build.properties"), new File(
                        mojo.baseDirectory + "/build.properties")));
    }
}
