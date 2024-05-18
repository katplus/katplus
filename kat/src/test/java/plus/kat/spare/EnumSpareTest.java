package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.actor.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class EnumSpareTest {

    enum Type {
        @Magic("PAGE_TYPE") PAGE,
        NONE,
        @Magic
        NULL,
        @Magic("POST_TYPE") POST
    }

    @Test
    public void test_base() throws IOException {
        Spare<Type> spare = new EnumSpare<>(
            "Type", Type.class, Supplier.ins()
        );

        assertNull(spare.read(Flow.of("\"\"")));
        assertNull(spare.read(Flow.of("\"null\"")));

        assertThrows(IOException.class, () -> spare.read(Flow.of("\"PAGE\"")));
        assertThrows(IOException.class, () -> spare.read(Flow.of("\"POST\"")));

        Type type0 = spare.read(
            Flow.of("\"NONE\"")
        );

        try (Chan chan = spare.write(type0, Flag.NORM)) {
            assertEquals("@Type \"NONE\"", chan.toString());
        }

        Type type1 = spare.read(
            Flow.of("\"NULL\"")
        );

        try (Chan chan = spare.write(type1, Flag.NORM)) {
            assertEquals("@Type \"NULL\"", chan.toString());
        }

        Type type2 = spare.read(
            Flow.of("\"POST_TYPE\"")
        );

        try (Chan chan = spare.write(type2, Flag.NORM)) {
            assertEquals("@Type \"POST_TYPE\"", chan.toString());
        }
    }
}
