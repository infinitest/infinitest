package jdave.test;

public class JDaveUtils
{
    public static Throwable createException()
    {
        try
        {
            throw new Exception("Test");
        }
        catch (Throwable e)
        {
            return e;
        }
    }
}
