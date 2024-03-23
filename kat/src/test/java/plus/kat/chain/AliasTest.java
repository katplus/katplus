package plus.kat.chain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class AliasTest {

    @SuppressWarnings("deprecation")
    static Alias alias(
        Alias ins, String alias
    ) {
        int size = alias.length();
        alias.getBytes(0, size, ins.flow(), 0);
        return ins.slip(size);
    }

    @Test
    public void test_isEmpty() {
        Alias a = new Alias(32);
        assertTrue(alias(a, "").isEmpty());
        assertFalse(alias(a, " ").isEmpty());
        assertFalse(alias(a, "kat").isEmpty());
        assertFalse(alias(a, "  kat  ").isEmpty());
    }

    @Test
    public void test_isBlank() {
        Alias a = new Alias(32);
        assertTrue(alias(a, "").isBlank());
        assertTrue(alias(a, " ").isBlank());
        assertFalse(alias(a, "kat").isBlank());
        assertFalse(alias(a, "  kat  ").isBlank());
    }
}
