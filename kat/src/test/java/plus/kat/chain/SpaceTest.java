package plus.kat.chain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SpaceTest {

    @SuppressWarnings("deprecation")
    static Space space(
        Space ins, String space
    ) {
        int size = space.length();
        space.getBytes(0, size, ins.flow(), 0);
        return ins.slip(size);
    }

    @Test
    public void test_set() {
        Space space = new Space(
            3, new byte[]{'k', 'a', 't'}
        );
        space.set(1, (byte) 'i');
        assertEquals('i', space.get(1));
        assertEquals("kit", space.toString());
        space.set(-3, (byte) '$');
        assertEquals('$', space.get(-3));
        assertEquals("$it", space.toString());

        assertThrows(IndexOutOfBoundsException.class, () -> space.set(3, (byte) 'i'));
        assertThrows(IndexOutOfBoundsException.class, () -> space.set(-4, (byte) 'i'));
    }

    @Test
    public void test_get() {
        Space space = new Space(
            3, new byte[]{'k', 'a', 't'}
        );
        assertEquals((byte) 'k', space.get(0));
        assertEquals((byte) 'a', space.get(1));
        assertEquals((byte) 't', space.get(2));
        assertEquals((byte) 't', space.get(-1));
        assertEquals((byte) 'a', space.get(-2));
        assertEquals((byte) 'k', space.get(-3));
        assertThrows(IndexOutOfBoundsException.class, () -> space.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> space.get(-4));
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
    public void test_isEmpty() {
        Space s = new Space(32);
        assertTrue(space(s, "").isEmpty());
        assertFalse(space(s, " ").isEmpty());
        assertFalse(space(s, "kat").isEmpty());
        assertFalse(space(s, "  kat  ").isEmpty());
    }

    @Test
    public void test_isBlank() {
        Space s = new Space(32);
        assertTrue(space(s, "").isBlank());
        assertTrue(space(s, " ").isBlank());
        assertFalse(space(s, "kat").isBlank());
        assertFalse(space(s, "  kat  ").isBlank());
    }

    @Test
    public void test_isClass() {
        Space s = new Space(64);
        assertTrue(space(s, "User").isClass());
        assertTrue(space(s, "kat.User").isClass());
        assertTrue(space(s, "kat.User$Name").isClass());
        assertTrue(space(s, "plus.kat.User").isClass());
        assertTrue(space(s, "plus.kat.v2.API").isClass());
        assertTrue(space(s, "plus.kat.v2.User").isClass());
        assertTrue(space(s, "plus.kat.User$Name").isClass());
        assertTrue(space(s, "plus.kat.entity.User").isClass());

        assertFalse(space(s, "").isClass());
        assertFalse(space(s, ".").isClass());
        assertFalse(space(s, "1").isClass());
        assertFalse(space(s, "$").isClass());
        assertFalse(space(s, "kat").isClass());
        assertFalse(space(s, "user").isClass());
        assertFalse(space(s, "plus.kat").isClass());
        assertFalse(space(s, "plus.kat").isClass());
        assertFalse(space(s, "plus.kat.v2").isClass());
        assertFalse(space(s, "plus.kat.entity").isClass());
        assertFalse(space(s, "plus.kat.user").isClass());
        assertFalse(space(s, "plus.kat.$User").isClass());
        assertFalse(space(s, "plus.kat.User$").isClass());
        assertFalse(space(s, "plus.kat._User").isClass());
        assertFalse(space(s, "plus.kat.User_").isClass());
        assertFalse(space(s, "plus.kat.User-").isClass());
        assertFalse(space(s, "plus.$kat.User").isClass());
        assertFalse(space(s, "plus._kat.User").isClass());
        assertFalse(space(s, "plus.kat.$User").isClass());
        assertFalse(space(s, "plus.kat.V2.User").isClass());
        assertFalse(space(s, "plus.kat..User").isClass());
        assertFalse(space(s, "plus.kat.User_Name").isClass());
        assertFalse(space(s, "plus.kat.User.name").isClass());
        assertFalse(space(s, "plus.kat.User-Name").isClass());
    }
}
