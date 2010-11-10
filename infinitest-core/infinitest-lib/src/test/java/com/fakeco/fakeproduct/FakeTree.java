package com.fakeco.fakeproduct;

import javax.swing.JTree;

@SuppressWarnings("all")
public class FakeTree extends JTree
{
    private final FakeProduct product;

    private final FakeDependency dep;

    public FakeTree()
    {
        product = new FakeProduct();
        setToolTipText(product.toString());
        dep = null;
        if (dep == null)
        {
        }
    }
}
