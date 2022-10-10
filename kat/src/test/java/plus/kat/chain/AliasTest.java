package plus.kat.chain;

import org.junit.jupiter.api.Test;
import plus.kat.stream.Bucket;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AliasTest {

    @Test
    public void test() {
        String name = "Alias";
        Alias alias = new Alias(name);
        assertSame(name, alias.toString());
        assertSame(alias.toString(), alias.toString());
    }

    @Test
    public void test2() {
        String name = "陆之岇";
        Alias alias = new Alias(name);
        assertSame(name, alias.toString());
        assertArrayEquals(name.getBytes(UTF_8), alias.toBytes());
    }

    @Test
    public void test_isFixed() {
        Alias a0 = new Alias("陆之岇");
        assertTrue(a0.isFixed());
        assertEquals(9, a0.length());

        Alias a1 = a0.copy();
        assertTrue(a1.isFixed());
        assertTrue(Alias.EMPTY.isFixed());

        assertTrue(new Alias(a0).isFixed());
        assertTrue(new Alias(new Value()).isFixed());
        assertTrue(new Alias(a1.toBytes()).isFixed());

        assertFalse(new Alias(a0, null).isFixed());
        assertFalse(new Alias((Bucket) null).isFixed());
    }

    @Test
    public void test_isMethod() {
        assertTrue(new Alias("kat").isMethod());
        assertTrue(new Alias("alias").isMethod());
        assertTrue(new Alias("isEmpty").isMethod());
        assertTrue(new Alias("hashCode").isMethod());

        assertFalse(new Alias("$").isMethod());
        assertFalse(new Alias("$age").isMethod());
        assertFalse(new Alias(".").isMethod());
        assertFalse(new Alias("1").isMethod());
        assertFalse(new Alias("_").isMethod());
        assertFalse(new Alias("_get").isMethod());
        assertFalse(new Alias("Getter").isMethod());
        assertFalse(new Alias("@alias").isMethod());
        assertFalse(new Alias("plus.kat").isMethod());
    }
}
