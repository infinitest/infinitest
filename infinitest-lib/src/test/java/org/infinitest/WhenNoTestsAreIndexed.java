package org.infinitest;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class WhenNoTestsAreIndexed
{
    @Test
    public void shouldAlwaysBeInScanningState()
    {
        ResultCollector collector = new ResultCollector(createMock(InfinitestCore.class));
        assertEquals(CoreStatus.SCANNING, collector.getStatus());
    }
}
