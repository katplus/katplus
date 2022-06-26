package plus.kat.stream;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ConvertTest {

    @Test
    public void test_byte_array_to_char() {
        byte[] d0 = "k".getBytes(UTF_8);
        assertEquals('k', Convert.toChar(d0, d0.length, '?'));

        byte[] d1 = "\n".getBytes(UTF_8);
        assertEquals('\n', Convert.toChar(d1, d1.length, '?'));

        byte[] d2 = "陆".getBytes(UTF_8);
        assertEquals('陆', Convert.toChar(d2, d2.length, '?'));

        byte[] d3 = "Σ".getBytes(UTF_8);
        assertEquals('Σ', Convert.toChar(d3, d3.length, '?'));
    }

}
