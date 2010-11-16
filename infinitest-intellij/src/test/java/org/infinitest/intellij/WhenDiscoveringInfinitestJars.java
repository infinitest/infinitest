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
package org.infinitest.intellij;

import static com.google.common.base.Charsets.*;
import static org.apache.commons.lang.StringUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Files;

public class WhenDiscoveringInfinitestJars
{
    private static final String MAVEN_VERSION = mavenVersion();
    private static final String INFINITEST_RUNNER_JAR = "infinitest-runner-" + MAVEN_VERSION + ".jar";
    private static final String INFINITEST_SNAPSHOT_JAR = "infinitest-lib-" + MAVEN_VERSION + ".jar";

    @Test
    public void shouldDetermineFileNamesFromEmbeddedPom()
    {
        InfinitestJarLocator locator = new InfinitestJarLocator();
        List<String> jarNames = locator.findInfinitestJarNames();
        assertThat(jarNames, hasItem(INFINITEST_SNAPSHOT_JAR));
        assertThat(jarNames, hasItem(INFINITEST_RUNNER_JAR));
    }

    @Test
    public void shouldDetermineInfinitestVersionFromEmbeddedPom()
    {
        InfinitestJarLocator locator = new InfinitestJarLocator();
        assertThat(locator.findInfinitestVersion(), is(MAVEN_VERSION));
    }

    private static String mavenVersion()
    {
        try
        {
            String pomXml = Files.toString(new File("pom.xml"), UTF_8);
            return substringBetween(pomXml, "<version>", "</version>");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to read maven version", e);
        }
    }
}
