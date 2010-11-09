package org.infinitest.intellij.plugin.swingui;

import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.*;
import static org.junit.Assert.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestInfinitestMainFrame
{
    private InfinitestMainFrame frame;

    @Before
    public void inContext()
    {
        frame = new InfinitestMainFrame();
    }

    @After
    public void cleanup()
    {
        frame = null;
    }

    @Test
    public void shouldDisplayProgress()
    {
        frame.setMaximumProgress(100);
        assertEquals(100, frame.getMaximumProgress());
        frame.setProgress(75);
    }

    @Test
    public void shouldUseUnknownColorToStart()
    {
        assertEquals(UNKNOWN_COLOR, frame.getProgressBarColor());
    }

    /**
     * Test harness to make sure the frame looks pretty.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args)
    {
        final TestInfinitestMainFrame test = new TestInfinitestMainFrame();
        test.inContext();

        final InfinitestMainFrame frame = test.frame;
        frame.setProgressBarColor(FAILING_COLOR);
        frame.setMaximumProgress(100);
        JPanel controlPanel = new JPanel(new FlowLayout());

        controlPanel.add(new JButton(new AbstractAction("Color")
        {
            private static final long serialVersionUID = -1L;

            public void actionPerformed(ActionEvent e)
            {
                if (frame.getProgressBarColor().equals(PASSING_COLOR))
                    frame.setProgressBarColor(FAILING_COLOR);
                else
                    frame.setProgressBarColor(PASSING_COLOR);
            }
        }));

        controlPanel.add(new JButton(new AbstractAction("%++")
        {
            private static final long serialVersionUID = -1L;

            public void actionPerformed(ActionEvent e)
            {
                frame.setProgress(test.frame.getProgress() + 5);
            }
        }));

        controlPanel.add(new JButton(new AbstractAction("Anger++")
        {
            private static final long serialVersionUID = -1L;

            public void actionPerformed(ActionEvent e)
            {
                frame.setAngerLevel(frame.getAngerLevel() + 1);
            }
        }));

        controlPanel.add(new JButton(new AbstractAction("%--")
        {
            private static final long serialVersionUID = -1L;

            public void actionPerformed(ActionEvent e)
            {
                frame.setProgress(test.frame.getProgress() - 5);
            }
        }));

        controlPanel.add(new JButton(new AbstractAction("Anger--")
        {
            private static final long serialVersionUID = -1L;

            public void actionPerformed(ActionEvent e)
            {
                frame.setAngerLevel(frame.getAngerLevel() - 1);
            }
        }));

        System.out.println(UIManager.getSystemLookAndFeelClassName());
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.setVisible(true);
    }
}
