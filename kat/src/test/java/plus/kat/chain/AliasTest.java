package plus.kat.chain;

import org.junit.jupiter.api.Test;

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
    public void test_isMethod() {
        assertTrue(new Alias("$").isMethod());
        assertTrue(new Alias("$age").isMethod());
        assertTrue(new Alias("alias").isMethod());
        assertTrue(new Alias("isEmpty").isMethod());
        assertTrue(new Alias("hashCode").isMethod());

        assertFalse(new Alias(".").isMethod());
        assertFalse(new Alias("1").isMethod());
        assertFalse(new Alias("@alias").isMethod());
        assertFalse(new Alias("plus.kat").isMethod());
    }
}
