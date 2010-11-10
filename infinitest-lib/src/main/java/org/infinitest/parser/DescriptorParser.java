package org.infinitest.parser;

class DescriptorParser
{
    public static String parseClassNameFromConstantPoolDescriptor(String descriptor)
    {
        String newDescriptor = descriptor.replaceAll("\\[", "");
        if (newDescriptor.startsWith("L"))
        {
            newDescriptor = newDescriptor.replaceFirst("L", "");
        }
        if (newDescriptor.length() == 1)
        {
            return Object.class.getName();
        }
        return newDescriptor.replace(";", "").replace('/', '.');
    }
}
