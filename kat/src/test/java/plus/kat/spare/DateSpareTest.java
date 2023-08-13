package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.chain.*;

import java.util.Date;
import java.io.IOException;

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
            assertEquals("\"2022-01-11 11:11:11.000\"", chan.toString());
        }

        Date date2 = spare.read(
            Flow.of("\"1641871353003\"").with(Flag.DIGIT_AS_DATE)
        );

        assertNotNull(date2);
        try (Chan chan = spare.write(date2)) {
            assertEquals("\"2022-01-11 11:22:33.003\"", chan.toString());
        }
    }

    Flag flag = v -> false;
    Value value = new Value(64);
    Spare<Date> spare = DateSpare.INSTANCE;

    @SuppressWarnings("deprecation")
    public void assertTest(
        String reader, String writer
    ) throws IOException {
        int size = reader.length();
        reader.getBytes(0, size, value.flow(), 0);

        Date date = spare.read(
            flag, value.slip(size)
        );
        try (Chan chan = spare.write(date)) {
            assertEquals(writer, chan.toString());
        }
    }

    @Test
    public void test_read_and_write() throws IOException {
        assertTest("2022.06.26", "\"2022-06-26 00:00:00.000\"");
        assertTest("2022-06-26", "\"2022-06-26 00:00:00.000\"");
        assertTest("2022/06/26", "\"2022-06-26 00:00:00.000\"");

        assertTest("2022-06-26T12:34", "\"2022-06-26 12:34:00.000\"");
        assertTest("2022.06.26T12:34", "\"2022-06-26 12:34:00.000\"");
        assertTest("2022/06/26T12:34", "\"2022-06-26 12:34:00.000\"");

        assertTest("2022.06.26 12:34:56", "\"2022-06-26 12:34:56.000\"");
        assertTest("2022-06-26 12:34:56", "\"2022-06-26 12:34:56.000\"");
        assertTest("2022/06/26 12:34:56", "\"2022-06-26 12:34:56.000\"");

        assertTest("2022-06-26 12:34:56.003", "\"2022-06-26 12:34:56.003\"");
        assertTest("2022/06/26 12:34:56.789", "\"2022-06-26 12:34:56.789\"");
        assertTest("2022/06/26T12:34:56.789", "\"2022-06-26 12:34:56.789\"");

        assertTest("2022-06-26 12:34:56.789Z", "\"2022-06-26 20:34:56.789\"");
        assertTest("2022-06-26T12:34:56.789Z", "\"2022-06-26 20:34:56.789\"");
        assertTest("2022/06/26T12:34:56.789Z", "\"2022-06-26 20:34:56.789\"");

        assertTest("2022-06-26T12:34:56.789-08:00", "\"2022-06-27 04:34:56.789\"");
        assertTest("2022-06-26T12:34:56.789-08:00", "\"2022-06-27 04:34:56.789\"");
        assertTest("2022/06/26T12:34:56.789-08:00", "\"2022-06-27 04:34:56.789\"");

        assertTest("2022-06-26T12:34:56.789+08:00", "\"2022-06-26 12:34:56.789\"");
        assertTest("2022-06-26T12:34:56.789+08:00", "\"2022-06-26 12:34:56.789\"");
        assertTest("2022/06/26T12:34:56.789+08:00", "\"2022-06-26 12:34:56.789\"");
    }
}
