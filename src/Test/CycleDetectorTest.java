package Test;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by s120342 on 26-5-2016.
 */
public class CycleDetectorTest {

    @Test
    public void testSimpleCycle() {
        assertSame("a", "a");
    }

}