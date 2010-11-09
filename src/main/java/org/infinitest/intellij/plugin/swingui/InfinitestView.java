package org.infinitest.intellij.plugin.swingui;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.tree.TreeModel;

/**
 * @author bjrady
 */
public interface InfinitestView
{
    void setAngerBasedOnTime(long timeSinceGreen);

    void setVisible(boolean b);

    void setProgress(int progress);

    void setProgressBarColor(Color yellow);

    void setMaximumProgress(int maxProgress);

    int getMaximumProgress();

    void setCycleTime(String timeStamp);

    void setCurrentTest(String testName);

    void addAction(Action action);

    void setResultsModel(TreeModel results);

    void setStatusMessage(String string);

    void writeLogMessage(String message);

    void writeError(String message);

    void addResultClickListener(ResultClickListener listener);
}
