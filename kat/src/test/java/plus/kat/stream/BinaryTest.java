package plus.kat.stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 */
public class BinaryTest {

    @Test
    public void test_apply() {
        Binary bin0 = new Binary();
        assertEquals(0, bin0.size);
        assertEquals(0, bin0.value.length);

        Binary bin1 = new Binary(3);
        assertEquals(0, bin1.size);
        assertEquals(3, bin1.value.length);

        byte[] text = "kraity"
            .getBytes(UTF_8);

        Binary bin2 = new Binary(text);
        assertEquals(6, bin2.size);
        assertSame(text, bin2.value);
        assertEquals(6, bin2.value.length);

        Binary bin3 = new Binary(text, 3);
        assertEquals(3, bin3.size);
        assertSame(text, bin3.value);
        assertEquals(6, bin3.value.length);
    }
}
