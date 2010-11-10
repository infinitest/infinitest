package org.infinitest.eclipse.trim;

import static com.google.common.collect.Sets.*;
import static org.eclipse.swt.SWT.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;

import java.util.Set;

import org.infinitest.CoreStatus;
import org.infinitest.TestQueueAdapter;
import org.infinitest.TestQueueEvent;
import org.infinitest.eclipse.status.WorkspaceStatus;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.springframework.stereotype.Component;

@Component
public class VisualStatusPresenter extends TestQueueAdapter implements VisualStatusRegistry
{
    private VisualStatus status;
    private final Set<String> testsRan = newLinkedHashSet();

    @Override
    public void testQueueUpdated(TestQueueEvent event)
    {
        if (!event.getTestQueue().isEmpty())
        {
            statusChanged(runningTests(event.getTestQueue().size(), event.getCurrentTest()));
        }
        else
        {
            statusChanged(testRunFinished(testsRan));
        }
    }

    public void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus)
    {
        switch (newStatus)
        {
        case PASSING:
            status.setBackgroundColor(COLOR_DARK_GREEN);
            status.setTextColor(COLOR_WHITE);
            break;
        case FAILING:
            setFailingColors();
            break;
        default:
            break;
        }
    }

    private void setFailingColors()
    {
        status.setBackgroundColor(COLOR_DARK_RED);
        status.setTextColor(COLOR_WHITE);
    }

    public void updateVisualStatus(VisualStatus status)
    {
        this.status = status;
    }

    public void filesSaved()
    {
        testsRan.clear();
    }

    public void testCaseComplete(TestCaseEvent event)
    {
        testsRan.add(event.getTestName());
        if (event.failed())
        {
            setFailingColors();
            // Remove this test from the set of tests currently being run.
        }

        // Keep in mind that tests can be interrupted before they are completed,
        // so testCaseStarting will be called again before this is called
    }

    public void testCaseStarting(TestEvent event)
    {
        // Add this test to the set of tests currently being run
    }

    public void statusChanged(WorkspaceStatus newStatus)
    {
        status.setText(newStatus.getMessage());
        status.setToolTip(newStatus.getToolTip());
        if (newStatus.warningMessage())
        {
            status.setBackgroundColor(COLOR_YELLOW);
            status.setTextColor(COLOR_BLACK);
        }
    }
}