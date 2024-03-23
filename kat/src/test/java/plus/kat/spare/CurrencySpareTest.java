package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flag;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class CurrencySpareTest {

    @Test
    public void test_base() throws IOException {
        Spare<Currency> spare = CurrencySpare.INSTANCE;

        Currency c0 = spare.read(
            Flow.of("CNY")
        );
        assertNotNull(c0);
        assertEquals("CNY", c0.getCurrencyCode());

        try (Chan chan = spare.write(c0)) {
            assertEquals("\"CNY\"", chan.toString());
        }

        Currency c1 = spare.read(
            Flow.of("USD")
        );
        assertNotNull(c1);
        assertEquals("USD", c1.getCurrencyCode());

        try (Chan chan = spare.write(c1, Flag.NORM)) {
            assertEquals("@Currency \"USD\"", chan.toString());
        }
    }
}
