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
package org.infinitest.testrunner.queue;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class TestQueue extends PriorityBlockingQueue<String>
{
    private static final long serialVersionUID = -1L;

    public TestQueue(Comparator<String> comparator)
    {
        super(11, comparator);
    }

    @Override
    public boolean add(String testName)
    {
        if (!contains(testName))
        {
            return super.add(testName);
        }
        return false;
    }
}
