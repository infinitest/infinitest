package org.infinitest.intellij.idea.window;

import javax.swing.JPanel;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.peer.PeerFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

public class IdeaWindowHelper
{
    @SuppressWarnings({ "deprecation" })
    public void addPanelToWindow(JPanel rootPanel, ToolWindow window)
    {
        ContentFactory contentFactory = PeerFactory.getInstance().getContentFactory();
        Content content = contentFactory.createContent(rootPanel, "Infinitest", false);
        window.getContentManager().addContent(content);
        window.setIcon(IconLoader.getIcon("/infinitest.png"));
    }
}
