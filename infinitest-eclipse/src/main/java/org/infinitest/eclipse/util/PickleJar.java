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
package org.infinitest.eclipse.util;

import static org.apache.commons.lang.SerializationUtils.*;

import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;

public abstract class PickleJar
{
    public static String pickle(Serializable object)
    {
        return new String(Base64.encodeBase64(serialize(object)));
    }

    public static Serializable unpickle(String stringForm)
    {
        return (Serializable) deserialize(Base64.decodeBase64(stringForm.getBytes()));
    }
}
