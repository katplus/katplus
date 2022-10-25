package plus.kat.chain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceTest {

    @Test
    public void test() {
        String name = "Space";
        Space space = new Space(name);
        assertSame(name, space.toString());
        assertSame(space.toString(), space.toString());
    }

    @Test
    public void test_isClass() {
        assertTrue(new Space("User").isClass());
        assertTrue(new Space("kat.User").isClass());
        assertTrue(new Space("kat.User$Name").isClass());
        assertTrue(new Space("plus.kat.User").isClass());
        assertTrue(new Space("plus.kat.v2.User").isClass());
        assertTrue(new Space("plus.kat.User$Name").isClass());
        assertTrue(new Space("plus.kat.entity.User").isClass());

        assertFalse(new Space(".").isClass());
        assertFalse(new Space("1").isClass());
        assertFalse(new Space("$").isClass());
        assertFalse(new Space("kat").isClass());
        assertFalse(new Space("user").isClass());
        assertFalse(new Space("plus.kat").isClass());
        assertFalse(new Space("plus.kat").isClass());
        assertFalse(new Space("plus.kat.v2").isClass());
        assertFalse(new Space("plus.kat.entity").isClass());
        assertFalse(new Space("plus.kat.user").isClass());
        assertFalse(new Space("plus.kat.$User").isClass());
        assertFalse(new Space("plus.kat.User$").isClass());
        assertFalse(new Space("plus.kat._User").isClass());
        assertFalse(new Space("plus.kat.User_").isClass());
        assertFalse(new Space("plus.kat.User-").isClass());
        assertFalse(new Space("plus.$kat.User").isClass());
        assertFalse(new Space("plus._kat.User").isClass());
        assertFalse(new Space("plus.kat.$User").isClass());
        assertFalse(new Space("plus.kat.V2.User").isClass());
        assertFalse(new Space("plus.kat..User").isClass());
        assertFalse(new Space("plus.kat.User_Name").isClass());
        assertFalse(new Space("plus.kat.User.name").isClass());
        assertFalse(new Space("plus.kat.User-Name").isClass());
    }

    @Test
    public void test_isPackage() {
        assertTrue(new Space("kat").isPackage());
        assertTrue(new Space("plus.kat").isPackage());
        assertTrue(new Space("plus.kat.v2").isPackage());
        assertTrue(new Space("plus.kat.entity").isPackage());

        assertFalse(new Space("plus.1").isPackage());
        assertFalse(new Space("User").isPackage());
        assertFalse(new Space(".").isPackage());
        assertFalse(new Space("$").isPackage());
        assertFalse(new Space("plus.1kat").isPackage());
        assertFalse(new Space("plus.$kat").isPackage());
        assertFalse(new Space("plus.kat.User").isPackage());
        assertFalse(new Space("plus.$kat.User").isPackage());
        assertFalse(new Space("plus._kat.User").isPackage());
        assertFalse(new Space("plus.kat.").isPackage());
        assertFalse(new Space("plus.kat..User").isPackage());
        assertFalse(new Space("plus.kat.entity.").isPackage());
    }
}
