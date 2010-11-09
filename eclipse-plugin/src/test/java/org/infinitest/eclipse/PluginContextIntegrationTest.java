package org.infinitest.eclipse;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IResourceChangeListener;
import org.infinitest.eclipse.workspace.ResourceFinder;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PluginContextIntegrationTest
{
    @Test
    public void shouldWireComponentsTogetherByTypeUsingSpringAutowiring()
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
                        "/META-INF//spring/plugin-context.xml", "/META-INF/spring/eclipse-test-context.xml" });
        // The main point of contact from Eclipse to Infinitest is this resource change listener, so
        // we check for that.
        assertFalse(context.getBeansOfType(IResourceChangeListener.class).isEmpty());

        assertFalse(context.getBeansOfType(ResourceFinder.class).isEmpty());
    }
}
