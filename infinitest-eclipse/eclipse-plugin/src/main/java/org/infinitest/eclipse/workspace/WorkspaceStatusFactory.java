package org.infinitest.eclipse.workspace;

import static java.text.DateFormat.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

import org.infinitest.eclipse.status.WorkspaceStatus;

public class WorkspaceStatusFactory
{
    public static WorkspaceStatus workspaceErrors()
    {
        return new WarningStatus("Infinitest disabled. Errors in workspace.");
    }

    public static WorkspaceStatus runningTests(int remainingTests, String currentTest)
    {
        String message = "Running " + stripPackageName(currentTest) + " (" + (remainingTests - 1) + " remaining)";
        return new TooltippedStatus(message, "Current test: " + currentTest);
    }

    public static WorkspaceStatus testRunFinished(Collection<String> testsRan)
    {
        String message = testsRan.size() + " test cases ran at " + timestamp();
        return new TooltippedStatus(message, "Tests Ran:\n" + listToMultilineString(testsRan));
    }

    public static WorkspaceStatus noTestsRun()
    {
        return new WarningStatus("No related tests found for last change.");
    }

    private static String timestamp()
    {
        return getTimeInstance(DateFormat.MEDIUM).format(new Date());
    }

    private static class SimpleStringStatus implements WorkspaceStatus
    {
        private final String message;

        public SimpleStringStatus(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }

        public String getToolTip()
        {
            return getMessage();
        }

        public boolean warningMessage()
        {
            return false;
        }
    }

    private static class WarningStatus extends SimpleStringStatus
    {
        public WarningStatus(String message)
        {
            super(message);
        }

        @Override
        public boolean warningMessage()
        {
            return true;
        }
    }

    private static class TooltippedStatus extends SimpleStringStatus
    {
        private final String tooltip;

        public TooltippedStatus(String message, String tooltip)
        {
            super(message);
            this.tooltip = tooltip;
        }

        @Override
        public String getToolTip()
        {
            return tooltip;
        }
    }

    public static WorkspaceStatus findingTests(int totalTestsFound)
    {
        return new SimpleStringStatus(totalTestsFound + " tests found so far");
    }
}
