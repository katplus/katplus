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
}
