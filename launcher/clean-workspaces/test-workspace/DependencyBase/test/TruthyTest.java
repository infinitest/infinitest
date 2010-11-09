import junit.framework.TestCase;

import org.test.depbase.Truth;

public class TruthyTest extends TestCase
{
    public void testShouldBeTrue()
    {
        assertTrue(new Truth().value());
    }
}
