package plus.kat.stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 */
public class BinaryTest {

    @Test
    public void test_lower_array() {
        byte[] b0 = new byte[]{'k', 'a', 't'};
        assertEquals("6b6174", Binary.toLower(b0));
        assertArrayEquals("6b6174".getBytes(US_ASCII), Binary.lower(b0));
    }

    @Test
    public void test_upper_array() {
        byte[] b0 = new byte[]{'k', 'a', 't'};
        assertEquals("6B6174", Binary.toUpper(b0));
        assertArrayEquals("6B6174".getBytes(US_ASCII), Binary.upper(b0));
    }

    @Test
    public void test_byte_array_size() {
        byte[] d0 = "kraity".getBytes(UTF_8);
        assertEquals(6, Binary.length(d0, 0, d0.length));

        byte[] d1 = "陆之岇".getBytes(UTF_8);
        assertEquals(3, Binary.length(d1, 0, d1.length));

        byte[] d2 = "😀".getBytes(UTF_8);
        assertEquals(2, Binary.length(d2, 0, d2.length));

        byte[] d3 = "陆之岇+😀+katplus".getBytes(UTF_8);
        assertEquals(14, Binary.length(d3, 0, d3.length));
    }
}
