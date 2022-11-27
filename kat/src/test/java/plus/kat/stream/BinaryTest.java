package plus.kat.stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 */
public class BinaryTest {

    @Test
    public void test_lower_byte() {
        assertEquals((byte) '6', Binary.lower(6));
        assertEquals((byte) 'a', Binary.lower(10));
        assertEquals((byte) 'v', Binary.lower(31));
    }

    @Test
    public void test_upper_byte() {
        assertEquals((byte) '6', Binary.upper(6));
        assertEquals((byte) 'A', Binary.upper(10));
        assertEquals((byte) 'V', Binary.upper(31));
    }

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
}
