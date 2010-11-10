package com.fakeco.fakeproduct;

import junit.framework.TestCase;

public class TestFakeTree extends TestCase
{
    private FakeTree tree;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        tree = new FakeTree();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        tree = null;
    }

    public void testTree()
    {
        assertNotNull(tree);
    }

}
