/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.intellij;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

public class InfinitestJarsLocator {
	private static final String BUNDLED_POM = "/META-INF/maven/org.infinitest/infinitest-intellij/pom.xml";


	public String findInfinitestRunnerJarName() {
		String version = findInfinitestVersion();
		return "infinitest-runner-" + version + ".jar";
	}
	
	public String findInfinitestClassLoaderlJarName() {
		String version = findInfinitestVersion();
		return "infinitest-classloader-" + version + ".jar";
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
	@Override
	public String getNamespaceURI(String prefix) {
		return "http://maven.apache.org/POM/4.0.0";
	}

	@Override
	public String getPrefix(String namespace) {
		return "pom";
	}

	@Override
	public Iterator<?> getPrefixes(String namespace) {
		return null;
	}
}
