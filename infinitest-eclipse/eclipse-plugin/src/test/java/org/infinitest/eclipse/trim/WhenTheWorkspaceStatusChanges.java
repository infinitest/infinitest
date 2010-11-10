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
