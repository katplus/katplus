package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.chain.*;

import java.util.Date;
import java.util.TimeZone;
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
            assertEquals("\"2022-01-11T11:11:11.000+08:00\"", chan.toString());
        }

        Date date2 = spare.read(
            Flow.of("\"1641871353003\"").and(Flag.DIGIT_AS_TIME)
        );

        assertNotNull(date2);
        try (Chan chan = spare.write(date2)) {
            assertEquals("\"2022-01-11T11:22:33.003+08:00\"", chan.toString());
        }

        Date date3 = spare.read(
            Flow.of("\"20220111T11:11:11\"")
        );

        assertNotNull(date3);
        try (Chan chan = spare.write(date3)) {
            assertEquals("\"2022-01-11T11:11:11.000+08:00\"", chan.toString());
        }

        Date date4 = spare.read(
            Flow.of("\"20220111T11:11:11.6\"")
        );

        assertNotNull(date4);
        try (Chan chan = spare.write(date4)) {
            assertEquals("\"2022-01-11T11:11:11.600+08:00\"", chan.toString());
        }

        Date date5 = spare.read(
            Flow.of("\"2022-01-11T11:11:11.0123456789Z\"")
        );

        assertNotNull(date5);
        try (Chan chan = spare.write(date5)) {
            assertEquals("\"2022-01-11T19:11:11.012+08:00\"", chan.toString());
        }

        Date date6 = spare.read(
            Flow.of("\"1111-11-11T11:11:11.0123456789Z\"")
        );

        assertNotNull(date6);
        try (Chan chan = spare.write(date6)) {
            assertEquals("\"1111-11-11T19:11:11.012+08:00\"", chan.toString());
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
            flag, value.slip(
                size, (byte) '"'
            )
        );
        try (Chan chan = spare.write(date)) {
            assertEquals(writer, chan.toString());
        }
    }

    @Test
    public void test_read_and_write() throws IOException {
        TimeZone zone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(
                TimeZone.getTimeZone("UTC")
            );
            assertTest("2022.06.26", "\"2022-06-26T00:00:00.000Z\"");
            assertTest("2022-06-26", "\"2022-06-26T00:00:00.000Z\"");
            assertTest("2022/06/26", "\"2022-06-26T00:00:00.000Z\"");

            assertTest("2022.06.26 12:34", "\"2022-06-26T12:34:00.000Z\"");
            assertTest("2022-06-26 12:34", "\"2022-06-26T12:34:00.000Z\"");
            assertTest("2022/06/26 12:34", "\"2022-06-26T12:34:00.000Z\"");

            TimeZone.setDefault(
                TimeZone.getTimeZone("GMT+08:00")
            );
            assertTest("2022.06.26T12:34", "\"2022-06-26T12:34:00.000+08:00\"");
            assertTest("2022-06-26T12:34", "\"2022-06-26T12:34:00.000+08:00\"");
            assertTest("2022/06/26T12:34", "\"2022-06-26T12:34:00.000+08:00\"");

            TimeZone.setDefault(
                TimeZone.getTimeZone("GMT+08:08")
            );
            assertTest("2022.06.26 12:34:56", "\"2022-06-26T12:34:56.000+08:08\"");
            assertTest("2022-06-26 12:34:56", "\"2022-06-26T12:34:56.000+08:08\"");
            assertTest("2022/06/26 12:34:56", "\"2022-06-26T12:34:56.000+08:08\"");

            TimeZone.setDefault(
                TimeZone.getTimeZone("GMT+08:38")
            );
            assertTest("2022.06.26T12:34:56", "\"2022-06-26T12:34:56.000+08:38\"");
            assertTest("2022-06-26T12:34:56", "\"2022-06-26T12:34:56.000+08:38\"");
            assertTest("2022/06/26T12:34:56", "\"2022-06-26T12:34:56.000+08:38\"");

            TimeZone.setDefault(
                TimeZone.getTimeZone("GMT+08:00")
            );
            assertTest("2022.06.26 12:34:56.789", "\"2022-06-26T12:34:56.789+08:00\"");
            assertTest("2022-06-26 12:34:56.789", "\"2022-06-26T12:34:56.789+08:00\"");
            assertTest("2022/06/26 12:34:56.789", "\"2022-06-26T12:34:56.789+08:00\"");

            assertTest("2022.06.26 12:34:56.789Z", "\"2022-06-26T20:34:56.789+08:00\"");
            assertTest("2022-06-26 12:34:56.789Z", "\"2022-06-26T20:34:56.789+08:00\"");
            assertTest("2022/06/26 12:34:56.789Z", "\"2022-06-26T20:34:56.789+08:00\"");

            assertTest("2022.06.26T12:34:56-0800", "\"2022-06-27T04:34:56.000+08:00\"");
            assertTest("2022-06-26T12:34:56-0800", "\"2022-06-27T04:34:56.000+08:00\"");
            assertTest("2022/06/26T12:34:56-0800", "\"2022-06-27T04:34:56.000+08:00\"");

            TimeZone.setDefault(
                TimeZone.getTimeZone("UTC")
            );
            assertTest("2022.06.26T12:34:56+08:00", "\"2022-06-26T04:34:56.000Z\"");
            assertTest("2022-06-26T12:34:56+08:00", "\"2022-06-26T04:34:56.000Z\"");
            assertTest("2022/06/26T12:34:56+08:00", "\"2022-06-26T04:34:56.000Z\"");

            assertTest("2022.06.26T12:34:56.789+0000", "\"2022-06-26T12:34:56.789Z\"");
            assertTest("2022-06-26T12:34:56.789+0000", "\"2022-06-26T12:34:56.789Z\"");
            assertTest("2022/06/26T12:34:56.789+0000", "\"2022-06-26T12:34:56.789Z\"");

            assertTest("2022.06.26T12:34:56.789+0800", "\"2022-06-26T04:34:56.789Z\"");
            assertTest("2022-06-26T12:34:56.789+0800", "\"2022-06-26T04:34:56.789Z\"");
            assertTest("2022/06/26T12:34:56.789+0800", "\"2022-06-26T04:34:56.789Z\"");

            assertTest("2022.06.26T12:34:56.789+00:00", "\"2022-06-26T12:34:56.789Z\"");
            assertTest("2022-06-26T12:34:56.789+00:00", "\"2022-06-26T12:34:56.789Z\"");
            assertTest("2022/06/26T12:34:56.789+00:00", "\"2022-06-26T12:34:56.789Z\"");

            assertTest("2022.06.26T12:34:56.789-08:00", "\"2022-06-26T20:34:56.789Z\"");
            assertTest("2022-06-26T12:34:56.789-08:00", "\"2022-06-26T20:34:56.789Z\"");
            assertTest("2022/06/26T12:34:56.789-08:00", "\"2022-06-26T20:34:56.789Z\"");

            assertTest("2022.06.26T12:34:56.789+08:00", "\"2022-06-26T04:34:56.789Z\"");
            assertTest("2022-06-26T12:34:56.789+08:00", "\"2022-06-26T04:34:56.789Z\"");
            assertTest("2022/06/26T12:34:56.789+08:00", "\"2022-06-26T04:34:56.789Z\"");
        } finally {
            TimeZone.setDefault(zone);
        }
    }
}
