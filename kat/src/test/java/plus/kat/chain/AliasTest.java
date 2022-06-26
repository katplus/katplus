package plus.kat.chain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AliasTest {
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
