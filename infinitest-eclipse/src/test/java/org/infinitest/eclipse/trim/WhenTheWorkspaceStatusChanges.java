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
package org.infinitest.eclipse.trim;

import static org.easymock.EasyMock.*;

import org.infinitest.eclipse.status.WorkspaceStatus;
import org.junit.Test;

public class WhenTheWorkspaceStatusChanges
{
    @Test
    public void shouldUpdateTheStatusBar()
    {
        VisualStatusPresenter presenter = new VisualStatusPresenter();
        VisualStatus statusBar = createMock(VisualStatus.class);
        presenter.updateVisualStatus(statusBar);
        statusBar.setText("New Status!");
        statusBar.setToolTip("Tooltip");
        replay(statusBar);

        presenter.statusChanged(new FakeStatus());

        verify(statusBar);
    }

    private final class FakeStatus implements WorkspaceStatus
    {
        public String getMessage()
        {
            return "New Status!";
        }

        public String getToolTip()
        {
            return "Tooltip";
        }

        public boolean warningMessage()
        {
            return false;
        }
    }
}
