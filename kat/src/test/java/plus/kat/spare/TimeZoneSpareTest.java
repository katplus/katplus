package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;

import java.util.TimeZone;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class TimeZoneSpareTest {

    @Test
    public void test_base() throws IOException {
        Spare<TimeZone> spare =
            TimeZoneSpare.INSTANCE;

        TimeZone tz = spare.read(
            Flow.of(
                "Asia/Shanghai"
            )
        );
        assertNotNull(tz);
        assertSame(
            spare, Spare.of(tz.getClass())
        );
        try (Chan chan = spare.write(tz, Flag.NORM)) {
            assertEquals("@TimeZone \"Asia/Shanghai\"", chan.toString());
        }
    }
}
