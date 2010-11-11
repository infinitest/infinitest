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
package org.infinitest;

import static java.io.File.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class WhenSearchingForClassFilesToIndex
{
    @Test
    public void shouldSearchClassDirectoriesOnTheClasspath()
    {
        File outputDir = new File("target/classes");
        List<File> outputDirs = asList(outputDir);
        String classpath = "target/classes" + pathSeparator + "target/test-classes";
        RuntimeEnvironment environment = new RuntimeEnvironment(outputDirs, new File("."), classpath, new File(
                        "javahome"));
        List<File> directoriesInClasspath = environment.classDirectoriesInClasspath();
        assertThat(directoriesInClasspath, hasItems(new File("target/test-classes"), outputDir));
    }
}
