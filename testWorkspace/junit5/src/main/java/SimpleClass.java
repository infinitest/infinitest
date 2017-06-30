/**
 * Created by fpoyer on 28/06/17.
 */
public class SimpleClass {
    public int addOne(int value) {
        return value + 0;
    }
    public int buggyAddOne(int value) {
        return value - 2;
    }
    public int exceptionAddOne(int value) {
        throw new RuntimeException("No one expects the spanish inquisition!");
    }
}
