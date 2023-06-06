package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Flow;
import plus.kat.Spare;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class AtomicSpareTest {

    @Test
    public void test_read_AtomicInteger() {
        Spare<AtomicInteger> spare
            = AtomicIntegerSpare.INSTANCE;

        assertNull(
            spare.read(Flow.of(""))
        );
        assertNull(
            spare.read(Flow.of("null"))
        );

        AtomicInteger atom = spare.read(
            Flow.of("143")
        );
        assertEquals(143, atom.get());
    }

    @Test
    public void test_read_AtomicLong() {
        Spare<AtomicLong> spare
            = AtomicLongSpare.INSTANCE;

        assertNull(
            spare.read(Flow.of(""))
        );
        assertNull(
            spare.read(Flow.of("null"))
        );

        AtomicLong atom = spare.read(
            Flow.of("14725836913579")
        );

        assertEquals(14725836913579L, atom.get());
    }

    @Test
    public void test_read_AtomicBoolean() {
        Spare<AtomicBoolean> spare = AtomicBooleanSpare.INSTANCE;

        assertNull(spare.read(Flow.of("")));
        assertNull(spare.read(Flow.of("null")));

        assertTrue(spare.read(Flow.of("1")).get());
        assertFalse(spare.read(Flow.of("0")).get());
        assertTrue(spare.read(Flow.of("true")).get());
        assertFalse(spare.read(Flow.of("false")).get());
    }
}
