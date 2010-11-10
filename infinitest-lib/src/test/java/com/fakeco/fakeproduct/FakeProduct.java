package com.fakeco.fakeproduct;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import com.fakeco.fakeproduct.id.FakeId;

@ClassAnnotation
public class FakeProduct
{
    @SuppressWarnings("unused")
    @FieldAnnotation
    private FakeId id;

    public FakeProduct()
    {
        id = new FakeId();
        // LogFactory.getLog(getClass()).warn("Warning");
    }

    public FakeProduct(FakeId id)
    {
        id.hashCode();
    }

    public int getSeven()
    {
        return 7;
    }

    @MethodAnnotation
    public void createInner()
    {
        StaticInnerClass clazz = new StaticInnerClass();
        clazz.actionPerformed(null);
    }

    public static void main(String[] args)
    {
        @SuppressWarnings("unused")
        List<Object> list;

        System.out.println("Hello World");
        FakeEnum fakeEnum = FakeEnum.ONE;
        fakeEnum.name();
    }

    public class TestNothing
    {
        public void testThisTestIsNotAUnitTestAndItShouldntBeRun()
        {
            throw new IllegalStateException();
        }
    }

    @SuppressWarnings("all")
    private static class StaticInnerClass extends AbstractAction
    {
        private StaticInnerClass()
        {
        }

        public void actionPerformed(ActionEvent e)
        {
            System.out.println("Hello World");
        }
    }
}
