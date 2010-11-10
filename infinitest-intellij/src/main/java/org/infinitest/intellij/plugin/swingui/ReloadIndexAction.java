package org.infinitest.intellij.plugin.swingui;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.infinitest.InfinitestCore;

public class ReloadIndexAction extends AbstractAction
{
    private static final long serialVersionUID = -1L;

    private final InfinitestCore core;

    public ReloadIndexAction(InfinitestCore core)
    {
        Icon icon = new ImageIcon(packageRelativeResource("reload.png", this.getClass()));
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.SHORT_DESCRIPTION, "Force rebuild of dependency graph");
        this.core = core;
    }

    public void actionPerformed(ActionEvent e)
    {
        core.reload();
    }

    private static URL packageRelativeResource(String resourceName, Class<?> clazz)
    {
        String directoryPrefix = '/' + clazz.getPackage().getName().replaceAll("\\.", "/") + '/';
        return clazz.getResource(directoryPrefix + resourceName);
    }
}
