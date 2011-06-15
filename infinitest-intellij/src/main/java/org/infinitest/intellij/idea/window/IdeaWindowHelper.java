/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.intellij.idea.window;

import javax.swing.JPanel;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.peer.PeerFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

public class IdeaWindowHelper
{
    public static final String WAITING_ICON_PATH = "/infinitest-waiting.png";

    public static final String RUNNING_ICON_PATH = "/infinitest.png";

    public static final String SUCCESS_ICON_PATH = "/infinitest-success.png";

    public static final String FAILURE_ICON_PATH = "/infinitest-failure.png";

    @SuppressWarnings({ "deprecation" })
    public void addPanelToWindow(JPanel rootPanel, ToolWindow window)
    {
        ContentFactory contentFactory = PeerFactory.getInstance().getContentFactory();
        Content content = contentFactory.createContent(rootPanel, "Infinitest", false);
        window.getContentManager().addContent(content);
        window.setIcon(IconLoader.getIcon(WAITING_ICON_PATH));
    }
}
