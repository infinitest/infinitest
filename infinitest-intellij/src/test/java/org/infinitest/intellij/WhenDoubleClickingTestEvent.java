package org.infinitest.intellij;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import org.infinitest.intellij.plugin.swingui.ResultClickListener;
import org.infinitest.testrunner.TestEvent;
import static org.infinitest.util.EventFakeSupport.createEvent;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;

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
