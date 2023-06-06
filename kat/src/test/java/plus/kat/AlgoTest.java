package plus.kat;

import org.junit.jupiter.api.Test;

import static plus.kat.Algo.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class AlgoTest {

    @Test
    public void test0() {
        assertEquals(KAT, KAT);
        assertEquals(DOC, DOC);
        assertEquals(JSON, JSON);
        assertNotEquals(KAT, JSON);
        assertNotEquals(DOC, JSON);
    }

    @Test
    public void test1() {
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
}
