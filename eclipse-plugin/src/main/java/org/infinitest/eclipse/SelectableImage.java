package org.infinitest.eclipse;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;

public interface SelectableImage
{
    void setImage(Image image);

    void addSelectionListener(SelectionListener listener);
}
