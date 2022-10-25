package plus.kat.kernel;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

public class ChainTest {

    @Test
    public void test_is() {
        assertTrue(new Alpha("$").is('$'));
        assertTrue(new Alpha("kat.plus").is(3, '.'));
        assertTrue(new Alpha("kat.plus").is(3, (byte) '.'));
        assertFalse(new Alpha("$").is('@'));
        assertFalse(new Alpha("$$").is('$'));
        assertFalse(new Alpha("//kat.plus").is(5, '$'));
        assertFalse(new Alpha("//kat.plus").is(5, (byte) '$'));

        String text = "@\uDFFF\uDEEE@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏";
        assertArrayEquals(text.getBytes(UTF_8), new Alpha(text).toBytes());
    }

    @Test
    public void test_get() {
        Alpha c = new Alpha("kat");
        byte def = '$';
        assertEquals((byte) 'k', c.get(0, def));
        assertEquals((byte) 'a', c.get(1, def));
        assertEquals((byte) 't', c.get(2, def));
        assertEquals((byte) '$', c.get(3, def));
        assertEquals((byte) 't', c.get(-1, def));
        assertEquals((byte) 'a', c.get(-2, def));
        assertEquals((byte) 'k', c.get(-3, def));
        assertEquals((byte) '$', c.get(-4, def));

        assertEquals((byte) 'k', c.get(0));
        assertEquals((byte) 'a', c.get(1));
        assertEquals((byte) 't', c.get(2));
        assertEquals((byte) 't', c.get(-1));
        assertEquals((byte) 'a', c.get(-2));
        assertEquals((byte) 'k', c.get(-3));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.get(3));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.get(-4));
    }

    @Test
    public void test_byteAt() {
        Alpha c = new Alpha("kat");
        assertEquals((byte) 'k', c.at(0));
        assertEquals((byte) 'a', c.at(1));
        assertEquals((byte) 't', c.at(2));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.at(3));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.at(-1));
    }

    @Test
    public void test_charAt() {
        Alpha c = new Alpha("kat");
        assertEquals('k', c.charAt(0));
        assertEquals('a', c.charAt(1));
        assertEquals('t', c.charAt(2));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.charAt(3));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.charAt(-1));
    }

    @Test
    public void test_blank() {
        StringBuilder sb = new StringBuilder();
        for (char i = 0; i < 256; i++) {
            if (Character.isWhitespace(i)) {
                sb.append((int) i).append(",");
            }
        }

        assertEquals("9,10,11,12,13,28,29,30,31,32,", sb.toString());
    }

    @Test
    public void test_toString() {
        Alpha v = new Alpha("plus");
        assertEquals(3444122, v.hashCode());
        assertSame(v.toString(), v.toString());

        v.join(".kat");
        assertSame(v.toString(), v.toString());
        assertEquals(-1850296438, v.hashCode());

        v.join(".Spare");
        assertSame(v.toString(), v.toString());
        assertEquals("plus.kat.Spare", v.toString());
        assertEquals(1559385939, v.hashCode());
        assertSame(v.toString(), v.toString(0, 14));

        assertEquals("s.k", v.toString(3, 6));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> v.toString(-1, 1));

        v.clear();
        assertSame("", v.toString());
        assertSame("", v.toString(0, 0));
        assertSame(v.toString(), v.toString());
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> v.toString(-1, 1));
    }

    @Test
    public void test_isEmpty() {
        assertTrue(new Alpha().isEmpty());
        assertTrue(new Alpha("").isEmpty());
        assertFalse(new Alpha(" ").isEmpty());
        assertFalse(new Alpha("kat").isEmpty());
        assertFalse(new Alpha("  kat  ").isEmpty());
    }

    @Test
    public void test_isBlank() {
        assertTrue(new Alpha().isBlank());
        assertTrue(new Alpha("").isBlank());
        assertTrue(new Alpha(" ").isBlank());
        assertFalse(new Alpha("kat").isBlank());
        assertFalse(new Alpha("  kat  ").isBlank());
    }

    @Test
    public void test_indexOf() {
        String string = "plus.kat.plus";
        Alpha value = new Alpha(string);

        assertEquals(0, value.indexOf((byte) 'p'));
        assertEquals(4, value.indexOf((byte) '.'));
        assertEquals(string.indexOf('.'), value.indexOf((byte) '.'));
        assertEquals(string.indexOf('k'), value.indexOf((byte) 'k'));

        assertEquals(string.indexOf('.', 10), value.indexOf((byte) '.', 10));
        assertEquals(string.indexOf('t', 10), value.indexOf((byte) 't', 10));
    }

    @Test
    public void test_lastIndexOf() {
        String string = "plus.kat.plus";
        Alpha value = new Alpha(string);

        assertEquals(string.lastIndexOf('.'), value.lastIndexOf((byte) '.'));
        assertEquals(string.lastIndexOf('k'), value.lastIndexOf((byte) 'k'));

        assertEquals(string.lastIndexOf('.', 1), value.lastIndexOf((byte) '.', 1));
        assertEquals(string.lastIndexOf('t', 1), value.lastIndexOf((byte) 't', 1));
    }

    @Test
    public void test_compareTo() {
        String s = "kat.plus";
        Alpha v = new Alpha(s);

        assertEquals(0, v.compareTo("kat.plus"));
        assertEquals(s.compareTo("+kat.plus"), v.compareTo("+kat.plus"));
        assertEquals(s.compareTo("kat.plus+"), v.compareTo("kat.plus+"));
        assertEquals(s.compareTo("+kat.plus+"), v.compareTo("+kat.plus+"));
    }

    @Test
    public void test_InputStream() throws IOException {
        Alpha v1 = new Alpha();
        try (InputStream in = new ByteArrayInputStream(
            "kat.plus".getBytes(UTF_8)
        )) {
            v1.join(in);
        }
        assertEquals("kat.plus", v1.toString());

        for (int i = 0; i < 128; i++) {
            v1.join(".kat.plus");
        }
        Alpha v2 = new Alpha();
        try (InputStream in = new ByteArrayInputStream(
            v1.value, 0, v1.count
        )) {
            v2.join(in);
        }

        String s1 = v1.toString();
        String s2 = v2.toString();
        assertEquals(s1.length(), s2.length());
        assertEquals(s1, s2);
    }

    @Test
    public void test_chain_InputStream() throws IOException {
        Alpha value = new Alpha();

        try (InputStream in = new ByteArrayInputStream("kraity".getBytes(UTF_8))) {
            value.join(in, 128);
        }

        assertEquals(6, value.length());
        assertEquals(6, value.capacity());
    }

    @Test
    public void test_grow() {
        Alpha a = new Alpha();
        byte[] b = "kat.plus".getBytes(ISO_8859_1);

        a.value = b;
        a.count = b.length;

        a.grow(b.length + 2);
        assertEquals(b.length, a.count);
        assertTrue(b.length + 2 <= a.value.length);
    }
}
