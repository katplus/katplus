package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flag;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class DateSpareTest {

    @Test
    public void test_base() throws IOException {
        Spare<Date> spare = DateSpare.INSTANCE;

        Date date1 = spare.read(
            Flow.of("\"2022-01-11 11:11:11\"")
        );

        assertNotNull(date1);
        try (Chan chan = spare.write(date1)) {
            assertEquals("\"2022-01-11 11:11:11\"", chan.toString());
        }

        Date date2 = spare.read(
            Flow.of("\"1641871353000\"").with(Flag.DIGIT_AS_DATE)
        );

        assertNotNull(date2);
        try (Chan chan = spare.write(date2)) {
            assertEquals("\"2022-01-11 11:22:33\"", chan.toString());
        }
    }
}
