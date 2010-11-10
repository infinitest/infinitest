package org.infinitest.intellij.idea.language;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.IconLoader;

class InfinitestGutterIconRenderer extends GutterIconRenderer
{
    private final InnerClassFriendlyTestEvent event;

    public InfinitestGutterIconRenderer(InnerClassFriendlyTestEvent event)
    {
        this.event = event;
    }

    @NotNull
    @Override
    public Icon getIcon()
    {
        return IconLoader.getIcon("/infinitest.png");
    }

    @Override
    public String getTooltipText()
    {
        String message = event.getMessage();

        message = formatMessage(message);

        return event.getErrorClassName() + "(" + message + ")";
    }

    private String formatMessage(String message)
    {
        if (message == null)
        {
            return "no message";
        }
        return message.replace("<", "&lt;").replace(">", "&gt;");
    }
}
