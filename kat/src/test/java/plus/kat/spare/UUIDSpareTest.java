package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flag;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class UUIDSpareTest {

    @Test
    public void test_base() throws IOException {
        Spare<UUID> spare
            = UUIDSpare.INSTANCE;

        assertNotNull(spare.apply());

        UUID uuid = spare.read(
            Flow.of(
                "092f7929-d2d6-44d6-9cc1-694c2e360c56"
            )
        );

        assertEquals("092f7929-d2d6-44d6-9cc1-694c2e360c56", uuid.toString());
        try (Chan chan = spare.write(uuid, Flag.NORM)) {
            assertEquals("@UUID \"092f7929-d2d6-44d6-9cc1-694c2e360c56\"", chan.toString());
        }
    }
}
