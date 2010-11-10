package org.infinitest.intellij;

import static org.hamcrest.Matchers.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;

import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.infinitest.intellij.plugin.swingui.ResultClickListener;
import org.infinitest.testrunner.TestEvent;
import org.junit.Test;

public class WhenDoubleClickingTestEvent
{
    private static final Exception EXCEPTION = new Exception();

    @Test
    public void shouldNavigateToSource()
    {
        FakeSourceNavigator navigator = new FakeSourceNavigator();

        ResultClickListener listener = new ResultClickListener(navigator);
        listener.mouseClicked(doubleClick(createEvent("Test", EXCEPTION)));

        assertThat(navigator.getClassName(), is(getClass().getName()));
        assertThat(navigator.getLine(), not(0));
    }

    @SuppressWarnings("serial")
    private MouseEvent doubleClick(final TestEvent event)
    {
        return new MouseEvent(new JTree()
        {
            @Override
            public TreePath getClosestPathForLocation(int x, int y)
            {
                return new TreePath(event);
            }
        }, 0, 0, 0, 0, 0, 2, false);
    }
}
