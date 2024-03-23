package plus.kat;

import org.junit.jupiter.api.Test;

import static plus.kat.Algo.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class AlgoTest {

    @Test
    public void test_build() {
        assertEquals(DOC, new Algo("xml"));
        assertEquals(JSON, new Algo("json"));

        assertEquals(kat, KAT.hashCode());
        assertEquals(doc, DOC.hashCode());
        assertEquals(json, JSON.hashCode());

        assertNotEquals(KAT, new Algo("KAT"));
        assertNotEquals(DOC, new Algo("DOC"));
        assertNotEquals(DOC, new Algo("XML"));

        assertThrows(
            Exception.class, () -> new Algo("\r\n")
        );
        assertThrows(
            Exception.class, () -> new Algo("text/kat")
        );
    }

    @Test
    public void test_of_name() {
        assertSame(KAT, Algo.of("kat"));
        assertSame(DOC, Algo.of("xml"));
        assertSame(JSON, Algo.of("json"));
        assertNotNull(Algo.of("yaml"));
        assertNotSame(KAT, Algo.of("KAT"));
        assertNotSame(DOC, Algo.of("XML"));
        assertNotSame(JSON, Algo.of("JSON"));
    }

    @Test
    public void test_toString() {
        assertEquals("Algo(kat)", KAT.toString());
        assertEquals("Algo(xml)", DOC.toString());
        assertEquals("Algo(json)", JSON.toString());
    }
}
