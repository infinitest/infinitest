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
package org.infinitest.parser;

import static java.util.Collections.*;

import java.io.File;
import java.util.Collection;

public class UnparsableClass implements JavaClass
{
    private final String classname;

    public UnparsableClass(String classname)
    {
        this.classname = classname;
    }

    public File getClassFile()
    {
        return null;
    }

    public Collection<String> getImports()
    {
        return emptyList();
    }

    public String getName()
    {
        return classname;
    }

    public boolean isATest()
    {
        return false;
    }

    public boolean locatedInClassFile()
    {
        return false;
    }

    public void dispose()
    {
    }

}
