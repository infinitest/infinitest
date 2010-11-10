package org.infinitest.intellij;

import static java.util.Arrays.*;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

public class InfinitestJarLocator
{
    private static final String BUNDLED_POM = "/META-INF/maven/org.infinitest/infinitest-intellij/pom.xml";

    public List<String> findInfinitestJarNames()
    {
        String version = findInfinitestVersion();
        return asList(
                "infinitest-lib-" + version + ".jar",
                "infinitest-runner-" + version + ".jar");
    }

    public String findInfinitestVersion()
    {
        try
        {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            xpath.setNamespaceContext(new PomNamespaceContext());
            XPathExpression expression = xpath
                    .compile("//pom:dependency[pom:artifactId = 'infinitest-lib']/pom:version");
            InputSource inputSource = new InputSource(getClass().getResourceAsStream(BUNDLED_POM));
            return expression.evaluate(inputSource);
        }
        catch (XPathExpressionException e)
        {
            throw new IllegalStateException("Unable to parse infinitest version from bundled pom", e);
        }
    }
}

/**
 * Always assign a prefix of pom regardless of namespace. This makes the locator insensitive to
 * whether or not a maven schema was used for the pom.
 */
class PomNamespaceContext implements NamespaceContext
{
    public String getNamespaceURI(String prefix)
    {
        return "http://maven.apache.org/POM/4.0.0";
    }

    public String getPrefix(String namespace)
    {
        return "pom";
    }

    public Iterator<?> getPrefixes(String namespace)
    {
        return null;
    }
}
