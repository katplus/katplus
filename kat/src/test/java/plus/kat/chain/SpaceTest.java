package plus.kat.chain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceTest {

    @Test
    public void test_type() {
        assertEquals("[B", new Space(byte[].class).getTypeName());
        assertEquals("java.lang.String", new Space(String.class).getTypeName());
        assertEquals("java.lang.Object", new Space(Object.class).getTypeName());
    }

    @Test
    public void test_isClass() {
        assertTrue(new Space("$").isClass());
        assertTrue(new Space("user").isClass());
        assertTrue(new Space("User").isClass());

        assertFalse(new Space(".").isClass());
        assertFalse(new Space("1").isClass());
        assertFalse(new Space("plus.kat").isClass());
    }

    @Test
    public void test_isPackage() {
        assertTrue(new Space("$").isPackage());
        assertTrue(new Space("user").isPackage());
        assertTrue(new Space("User").isPackage());
        assertTrue(new Space("plus.$kat").isPackage());
        assertTrue(new Space("plus.kat.User").isPackage());

        assertFalse(new Space(".").isPackage());
        assertFalse(new Space("plus.1kat").isPackage());
        assertFalse(new Space("plus.$kat.User").isPackage());
        assertFalse(new Space("plus._kat.User").isPackage());
        assertFalse(new Space("plus.kat.").isPackage());
        assertFalse(new Space("plus.kat..User").isPackage());
    }
}
