package org.infinitest.intellij.idea.facet;

import org.jdom.Element;

public class ConfigurationElementBuilder
{
    private final Element configElement = new Element("config");

    public static ConfigurationElementBuilder configuration()
    {
        return new ConfigurationElementBuilder();
    }

    public ConfigurationElementBuilder withScmUpdate(boolean scmUpdate)
    {
        configElement.setAttribute("scmUpdateGreenHook", Boolean.toString(scmUpdate));
        return this;
    }

    public ConfigurationElementBuilder withLicenseKey(String key)
    {
        Element license = new Element("license");
        license.addContent(key);

        configElement.addContent(license);
        return this;
    }

    public Element build()
    {
        return configElement;
    }
}
