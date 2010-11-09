package org.infinitest.intellij.plugin.launcher;

import static java.awt.Color.*;
import static org.infinitest.intellij.plugin.launcher.StatusMessages.*;

import java.awt.Color;
import java.util.Collection;

import org.infinitest.CoreStatus;
import org.infinitest.FailureListListener;
import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.StatusChangeListener;
import org.infinitest.TestControl;
import org.infinitest.TestQueueEvent;
import org.infinitest.TestQueueListener;
import org.infinitest.intellij.InfinitestAnnotator;
import org.infinitest.intellij.plugin.swingui.HaltTestAction;
import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.infinitest.intellij.plugin.swingui.ReloadIndexAction;
import org.infinitest.intellij.plugin.swingui.SwingEventQueue;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.util.InfinitestUtils;

/**
 * An automated test runner for JUnit Tests.
 * 
 * @author <a href="mailto:benrady@gmail.com">Ben Rady</a>
 */
public class InfinitestPresenter implements StatusChangeListener, TestQueueListener, FailureListListener
{
    private final InfinitestView view;
    private final StateMonitor monitor;
    private final ResultCollector resultCollector;
    private final InfinitestAnnotator annotator;

    public static final Color PASSING_COLOR = new Color(0x359b35);

    public static final Color FAILING_COLOR = RED;

    public static final Color UNKNOWN_COLOR = YELLOW;

    public static final Color WAITING_COLOR = BLACK;

    public InfinitestPresenter(ResultCollector resultCollector, InfinitestCore core, InfinitestView infinitestView,
                    TestControl control, InfinitestAnnotator annotator)
    {
        this.resultCollector = resultCollector;
        this.annotator = annotator;
        view = infinitestView;
        resultCollector.addStatusChangeListener(this);
        resultCollector.addChangeListener(this);
        core.addTestQueueListener(this);
        view.addAction(new ReloadIndexAction(core));
        view.addAction(new HaltTestAction(control));
        monitor = new StateMonitor();
        resultCollector.addStatusChangeListener(monitor);
        updateStatus();
        indicateWaitingForChanges();
    }

    private void indicateWaitingForChanges()
    {
        view.setProgressBarColor(BLACK);
        view.setMaximumProgress(1);
        view.setProgress(1);
    }

    public void coreStatusChanged(CoreStatus oldStatus, final CoreStatus newStatus)
    {
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                updateView(newStatus);
            }
        };
        new SwingEventQueue().pushAndWait(runnable);
    }

    public void testQueueUpdated(TestQueueEvent event)
    {
        view.setMaximumProgress(event.getInitialSize());
        view.setProgress(1 + event.getTestsRun());
        if (!event.getTestQueue().isEmpty())
            view.setCurrentTest(event.getCurrentTest());
    }

    private void updateView(CoreStatus status)
    {
        view.setStatusMessage(StatusMessages.getMessage(status));
        switch (status)
        {
        case RUNNING:
            view.setProgressBarColor(UNKNOWN_COLOR);
            break;
        case FAILING:
            view.setProgressBarColor(FAILING_COLOR);
            view.setProgress(view.getMaximumProgress());
            view.setCurrentTest("");
            break;
        case PASSING:
            view.setProgressBarColor(PASSING_COLOR);
            view.setProgress(view.getMaximumProgress());
            view.setCurrentTest("");
            break;
        case INDEXING:
            view.setProgressBarColor(UNKNOWN_COLOR);
            view.setProgress(view.getMaximumProgress());
            break;
        case SCANNING:
            // No additional updates needed
            break;
        default:
            throw new IllegalArgumentException("Unknown status " + status);
        }
    }

    public void updateStatus()
    {
        long cycleTime = monitor.getCycleLengthInMillis();
        String timeStamp = InfinitestUtils.formatTime(cycleTime);
        view.setAngerBasedOnTime(cycleTime);
        view.setCycleTime(timeStamp);
        view.setStatusMessage(getMessage(resultCollector.getStatus()));
    }

    public void reloading()
    {
        // nothing to do here
    }

    public void testRunComplete()
    {
        // nothing to do here
    }

    public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved)
    {
        for (TestEvent added : failuresAdded)
            annotator.annotate(added);
        for (TestEvent removed : failuresRemoved)
            annotator.clearAnnotation(removed);
    }

    public void failuresUpdated(Collection<TestEvent> updatedFailures)
    {
        for (TestEvent updated : updatedFailures)
        {
            annotator.clearAnnotation(updated);
            annotator.annotate(updated);
        }
    }
}
