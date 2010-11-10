package org.infinitest.util;

import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.fakeco.fakeproduct.FakeProduct;

public class TestBackupAndRestore
{
    @Test
    public void shouldBackupAndRestoreClasses() throws Exception
    {
        String className = FakeProduct.class.getName();
        File backup = createBackup(className);
        File file = getFileForClass(className);
        assertTrue(file.delete());

        InfinitestTestUtils.restoreFromBackup(backup);
        assertTrue(file.exists());
    }
}
