package plus.kat.stream;

import org.junit.jupiter.api.Test;

import static plus.kat.stream.Convert.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 */
public class ConvertTest {

    @Test
    public void test_byte_array_to_char() {
        byte[] d0 = "k".getBytes(UTF_8);
        assertEquals('k', Convert.toChar(d0, d0.length, '\0'));

        byte[] d1 = "\n".getBytes(UTF_8);
        assertEquals('\n', Convert.toChar(d1, d1.length, '\0'));

        byte[] d2 = "Σ".getBytes(UTF_8);
        assertEquals(2, d2.length);
        assertEquals('\0', Convert.toChar(d2, 1, '\0'));
        assertEquals('Σ', Convert.toChar(d2, d2.length, '\0'));

        byte[] d3 = "陆".getBytes(UTF_8);
        assertEquals(3, d3.length);
        assertEquals('\0', Convert.toChar(d3, 1, '\0'));
        assertEquals('\0', Convert.toChar(d3, 2, '\0'));
        assertEquals('陆', Convert.toChar(d3, d3.length, '\0'));
        assertEquals('\0', Convert.toChar(new byte[]{d3[1], d3[2]}, 2, '\0'));
    }

    @Test
    public void test_byte_array_to_char_array() {
        byte[] d0 = "kraity".getBytes(UTF_8);
        assertEquals("kraity", new String(toChars(d0, 0, d0.length)));

        byte[] d1 = "陆之岇".getBytes(UTF_8);
        assertEquals("陆之岇", new String(toChars(d1, 0, d1.length)));

        byte[] d2 = "😀".getBytes(UTF_8);
        assertEquals("😀", new String(toChars(d2, 0, d2.length)));

        byte[] d3 = "陆之岇+😀+katplus".getBytes(UTF_8);
        assertEquals("陆之岇+😀+katplus", new String(toChars(d3, 0, d3.length)));

        byte[] d4 = "@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏".getBytes(UTF_8);
        assertEquals("@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏", new String(toChars(d4, 0, d4.length)));
    }
}
