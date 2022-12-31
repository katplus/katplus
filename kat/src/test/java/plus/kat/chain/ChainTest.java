package plus.kat.chain;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

public class ChainTest {

    @Test
    public void test_is() {
        assertTrue(new Chain("$").is('$'));
        assertTrue(new Chain("kat.plus").is(3, '.'));
        assertTrue(new Chain("kat.plus").is(3, (byte) '.'));
        assertFalse(new Chain("$").is('@'));
        assertFalse(new Chain("$$").is('$'));
        assertFalse(new Chain("//kat.plus").is(5, '$'));
        assertFalse(new Chain("//kat.plus").is(5, (byte) '$'));

        String text = "@\uDFFF\uDEEE@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏";
        assertArrayEquals(text.getBytes(UTF_8), new Chain(text).toBytes());
    }

    @Test
    public void test_set() {
        Chain c = new Chain("kat");
        c.set(1, (byte) 'i');
        assertEquals("kit", c.toString());
        c.set(-3, (byte) '$');
        assertEquals("$it", c.toString());

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.set(3, (byte) 'i'));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.set(-4, (byte) 'i'));
    }

    @Test
    public void test_get() {
        Chain c = new Chain("kat");
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
    public void test_slip() {
        Chain c = new Chain("plus.kat");
        assertEquals(8, c.length());

        assertThrows(IndexOutOfBoundsException.class, () -> c.slip(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> c.slip(12));

        c.slip(3);
        assertEquals(3, c.length());
        c.slip(0);
        assertTrue(c.isEmpty());
        assertEquals(0, c.length());
    }

    @Test
    public void test_byteAt() {
        Chain c = new Chain("kat");
        assertEquals((byte) 'k', c.at(0));
        assertEquals((byte) 'a', c.at(1));
        assertEquals((byte) 't', c.at(2));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.at(3));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> c.at(-1));
    }

    @Test
    public void test_charAt() {
        Chain c = new Chain("kat");
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
        Chain v = new Chain("plus");
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
        assertTrue(new Chain().isEmpty());
        assertTrue(new Chain("").isEmpty());
        assertFalse(new Chain(" ").isEmpty());
        assertFalse(new Chain("kat").isEmpty());
        assertFalse(new Chain("  kat  ").isEmpty());
    }

    @Test
    public void test_isBlank() {
        assertTrue(new Chain().isBlank());
        assertTrue(new Chain("").isBlank());
        assertTrue(new Chain(" ").isBlank());
        assertFalse(new Chain("kat").isBlank());
        assertFalse(new Chain("  kat  ").isBlank());
    }

    @Test
    public void test_indexOf() {
        String string = "plus.kat.plus";
        Chain value = new Chain(string);

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
        Chain value = new Chain(string);

        assertEquals(string.lastIndexOf('.'), value.lastIndexOf((byte) '.'));
        assertEquals(string.lastIndexOf('k'), value.lastIndexOf((byte) 'k'));

        assertEquals(string.lastIndexOf('.', 1), value.lastIndexOf((byte) '.', 1));
        assertEquals(string.lastIndexOf('t', 1), value.lastIndexOf((byte) 't', 1));
    }

    @Test
    public void test_compareTo() {
        String s = "kat.plus";
        Chain v = new Chain(s);

        assertEquals(0, v.compareTo("kat.plus"));
        assertEquals(s.compareTo("+kat.plus"), v.compareTo("+kat.plus"));
        assertEquals(s.compareTo("kat.plus+"), v.compareTo("kat.plus+"));
        assertEquals(s.compareTo("+kat.plus+"), v.compareTo("+kat.plus+"));
    }

    @Test
    public void test_InputStream() throws IOException {
        Chain v1 = new Chain();
        try (InputStream in = new ByteArrayInputStream(
            "kat.plus".getBytes(UTF_8)
        )) {
            v1.join(in);
        }
        assertEquals("kat.plus", v1.toString());

        for (int i = 0; i < 128; i++) {
            v1.join(".kat.plus");
        }
        Chain v2 = new Chain();
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
    public void test_join_Reader() throws IOException {
        byte[] source = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            source[i] = (byte) (i % 128);
        }

        Chain value = new Chain();
        Reader reader = new InputStreamReader(
            new ByteArrayInputStream(source)
        );

        value.join(reader);
        assertEquals(-1, reader.read());
        assertArrayEquals(source, value.toBytes());
    }

    @Test
    public void test_join_ByteBuffer() {
        byte[] source = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            source[i] = (byte) (i % 128);
        }

        Chain value = new Chain();
        ByteBuffer buffer =
            ByteBuffer.wrap(source);

        value.join(buffer);
        assertFalse(buffer.hasRemaining());
        assertArrayEquals(source, value.toBytes());
    }

    @Test
    public void test_join_InputStream() throws IOException {
        byte[] source = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            source[i] = (byte) (i % 128);
        }

        Chain value = new Chain();
        InputStream stream =
            new ByteArrayInputStream(source);

        value.join(stream);
        assertEquals(-1, stream.read());
        assertArrayEquals(source, value.toBytes());
    }

    @Test
    public void test_chain_InputStream() throws IOException {
        Chain value = new Chain();

        try (InputStream in = new ByteArrayInputStream("kraity".getBytes(UTF_8))) {
            value.join(in, 128);
        }

        assertEquals(6, value.length());
        assertEquals(6, value.capacity());
    }

    @Test
    public void test_grow() {
        Chain a = new Chain();
        byte[] b = "kat.plus".getBytes(ISO_8859_1);

        a.value = b;
        a.count = b.length;

        a.grow(b.length + 2);
        assertEquals(b.length, a.count);
        assertTrue(b.length + 2 <= a.value.length);
    }
}
