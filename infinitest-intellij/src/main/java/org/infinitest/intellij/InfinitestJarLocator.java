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

import static java.util.Arrays.*;

import java.util.*;

import javax.xml.namespace.*;
import javax.xml.xpath.*;

import org.xml.sax.*;

public class InfinitestJarLocator {
	private static final String BUNDLED_POM = "/META-INF/maven/org.infinitest/infinitest-intellij/pom.xml";

	public List<String> findInfinitestJarNames() {
		String version = findInfinitestVersion();
		return asList("infinitest-lib-" + version + ".jar", "infinitest-runner-" + version + ".jar");
	}

	public String findInfinitestVersion() {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(new PomNamespaceContext());
			XPathExpression expression = xpath.compile("//pom:dependency[pom:artifactId = 'infinitest-lib']/pom:version");
			InputSource inputSource = new InputSource(getClass().getResourceAsStream(BUNDLED_POM));
			return expression.evaluate(inputSource);
		} catch (XPathExpressionException e) {
			throw new IllegalStateException("Unable to parse infinitest version from bundled pom", e);
		}
	}
}

/**
 * Always assign a prefix of pom regardless of namespace. This makes the locator
 * insensitive to whether or not a maven schema was used for the pom.
 */
class PomNamespaceContext implements NamespaceContext {
	public String getNamespaceURI(String prefix) {
		return "http://maven.apache.org/POM/4.0.0";
	}

	public String getPrefix(String namespace) {
		return "pom";
	}

	public Iterator<?> getPrefixes(String namespace) {
		return null;
	}
}
