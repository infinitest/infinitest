package org.infinitest.intellij;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.tree.TreeModel;

import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.infinitest.intellij.plugin.swingui.ResultClickListener;

@SuppressWarnings("all")
public class FakeInfinitestView implements InfinitestView
{
    public void setAngerBasedOnTime(long timeSinceGreen)
    {
    }

    public void setVisible(boolean b)
    {
    }

    public void setProgress(int progress)
    {
    }

    public void setProgressBarColor(Color yellow)
    {
    }

    public void setMaximumProgress(int maxProgress)
    {
    }

    public int getMaximumProgress()
    {
        return 0;
    }

    public void setCycleTime(String timeStamp)
    {
    }

    public void setCurrentTest(String testName)
    {
    }

    public void addAction(Action action)
    {
    }

    public void setResultsModel(TreeModel results)
    {
    }

    public void setStatusMessage(String string)
    {
    }

    public void writeLogMessage(String message)
    {
    }

    public void writeError(String message)
    {
    }

    public void addResultClickListener(ResultClickListener listener)
    {
    }
}
