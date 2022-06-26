package plus.kat.kernel;

import org.junit.jupiter.api.Test;
import plus.kat.chain.Value;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class ChainTest {

    @Test
    public void test_is() {
        assertTrue(new Value("$").is('$'));
        assertTrue(new Value("$").is((byte) '$'));
        assertTrue(new Value("kat.plus").is("kat.plus"));

        assertFalse(new Value("$$").is('$'));
        assertFalse(new Value("陆之岇").is("陆之岇"));
        assertFalse(new Value("//kat.plus").is("kat.plus"));
    }

    @Test
    public void test_digest() throws NoSuchAlgorithmException {
        String text = "User{i:id(1)s:name(kraity)}";
        Value value = new Value(text);
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", new Value(0).digest());
        assertEquals("d04f45fd1805ea7a98821bdad6894cb4", value.digest());
        assertEquals("21707be3777f237901b7edcdd73dc8288a81a4d2", value.digest("SHA1"));
    }

    @Test
    public void test_startWith() {
        Value value = new Value("plus.kat");
        assertTrue(value.startWith("plus"));
        assertTrue(value.startWith("plus."));

        assertFalse(value.startWith("kat"));
        assertFalse(value.startWith(".plus"));
        assertFalse(value.startWith("kat.plus"));
        assertFalse(value.startWith("plus$kat"));
        assertFalse(value.startWith("$plus$kat$"));
    }

    @Test
    public void test_endsWith() {
        Value value = new Value("plus.kat");
        assertTrue(value.endsWith("kat"));
        assertTrue(value.endsWith(".kat"));

        assertFalse(value.endsWith("plus"));
        assertFalse(value.endsWith("kat."));
        assertFalse(value.endsWith("kat.plus"));
        assertFalse(value.endsWith("$plus$kat$"));
    }

    @Test
    public void test_indexOf() {
        Value value = new Value("plus.kat.plus");

        assertEquals("plus.kat.plus".indexOf('.'), value.indexOf('.'));
        assertEquals("plus.kat.plus".indexOf('k'), value.indexOf('k'));
        assertEquals("plus.kat.plus".indexOf("p"), value.indexOf("p"));
        assertEquals("plus.kat.plus".indexOf("k"), value.indexOf("k"));
        assertEquals("plus.kat.plus".indexOf("kat"), value.indexOf("kat"));

        assertEquals("plus.kat.plus".indexOf('.', 10), value.indexOf('.', 10));
        assertEquals("plus.kat.plus".indexOf("kat", 10), value.indexOf("kat", 10));

    }

    @Test
    public void test_lastIndexOf() {
        Value value = new Value("plus.kat.plus");

        assertEquals("plus.kat.plus".lastIndexOf('.'), value.lastIndexOf('.'));
        assertEquals("plus.kat.plus".lastIndexOf('k'), value.lastIndexOf('k'));
        assertEquals("plus.kat.plus".lastIndexOf("p"), value.lastIndexOf("p"));
        assertEquals("plus.kat.plus".lastIndexOf("k"), value.lastIndexOf("k"));
        assertEquals("plus.kat.plus".lastIndexOf("kat"), value.lastIndexOf("kat"));

        assertEquals("plus.kat.plus".lastIndexOf('.', 1), value.lastIndexOf('.', 1));
        assertEquals("plus.kat.plus".lastIndexOf("kat", 1), value.lastIndexOf("kat", 1));

    }

    @Test
    public void test_contains() {
        Value value = new Value("plus.kat.plus");

        assertTrue(value.contains('.'));
        assertTrue(value.contains("kat"));
        assertTrue(value.contains("kat", 3));
        assertTrue(value.contains("plus.kat.plus"));

        assertFalse(value.contains('.', 10));
        assertFalse(value.contains("kat", 10));
        assertFalse(value.contains("$plus.kat.plus"));

    }

}
