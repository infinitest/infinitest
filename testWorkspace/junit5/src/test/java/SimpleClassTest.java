import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by fpoyer on 28/06/17.
 */
public class SimpleClassTest {
    private SimpleClass simpleClass;

    @BeforeEach
    public void setup() {
        simpleClass = new SimpleClass();
    }

    @Test
    public void simplePublicTest() {
        assertThat(simpleClass.addOne(1)).isEqualTo(2);
    }

    @Test
    void simplePackageTest() {
        assertThat(simpleClass.addOne(1)).isEqualTo(2);
    }

    @Test
    public void failingTest() {
        assertThat(simpleClass.buggyAddOne(1)).isEqualTo(2);
    }

    @Test
    public void expectionTest() {
        assertThatThrownBy(() -> simpleClass.exceptionAddOne(1)).isInstanceOf(RuntimeException.class);
    }
}
