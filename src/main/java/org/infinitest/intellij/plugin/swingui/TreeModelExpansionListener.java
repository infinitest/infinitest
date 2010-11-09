package org.infinitest.intellij.plugin.swingui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.JTree;
import javax.swing.event.TreeModelListener;

/**
 * @author <a href="mailto:benrady@gmail.com">Ben Rady</a>
 */
class TreeModelExpansionListener
{
    public static void watchTree(final JTree tree)
    {
        tree.expandPath(tree.getPathForRow(0));
        Class<?>[] interfaces = new Class<?>[] { TreeModelListener.class };
        InvocationHandler handler = new InvocationHandler()
        {
            public Object invoke(Object proxy, Method method, Object[] args)
            {
                if ("equals".equals(method.getName()))
                    return TreeModelExpansionListener.class.equals(args[0]);
                if ("hashCode".equals(method.getName()))
                    return TreeModelExpansionListener.class.hashCode();

                expandTreeNodes(tree);
                return null;
            }
        };
        ClassLoader classLoader = tree.getClass().getClassLoader();
        TreeModelListener listener = (TreeModelListener) Proxy.newProxyInstance(classLoader, interfaces, handler);
        tree.getModel().addTreeModelListener(listener);
    }

    private static void expandTreeNodes(JTree tree)
    {
        for (int i = 0; i < tree.getRowCount(); i++)
            tree.expandRow(i);
    }
}
