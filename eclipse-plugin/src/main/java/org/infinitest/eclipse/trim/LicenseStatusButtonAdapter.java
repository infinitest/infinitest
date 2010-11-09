package org.infinitest.eclipse.trim;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;
import org.infinitest.eclipse.SelectableImage;

public class LicenseStatusButtonAdapter implements SelectableImage
{
    private final Label label;

    public LicenseStatusButtonAdapter(Label label)
    {
        this.label = label;
    }

    public void setImage(Image image)
    {
        label.setImage(image);
    }

    public void addSelectionListener(SelectionListener listener)
    {
        label.addMouseListener(new MouseToSelectionAdapter(listener));
        label.getParent().pack();
        label.getParent().layout();
    }
}
