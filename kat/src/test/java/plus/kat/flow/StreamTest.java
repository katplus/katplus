package plus.kat.flow;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class StreamTest {

    @Test
    public void test_close() throws IOException {
        try (Stream ignored = new Stream() {
            {
                assertNotNull(bucket);
                emit(1);
                emit(',');
                emit("kraity");
                assertEquals(
                    "1,kraity", toString()
                );
                close();
                assertEquals(0, size);
                assertEquals(0, value.length);
            }
        }) {

        }
    }

    @Test
    public void test_number() throws IOException {
        try (Stream flux = new Stream()) {
            flux.emit(0F);
            flux.emit(',');
            flux.emit(0D);
            flux.emit(',');
            flux.emit(1F);
            flux.emit(',');
            flux.emit(1D);
            flux.emit(',');
            flux.emit(-1F);
            flux.emit(',');
            flux.emit(-1D);
            flux.emit(',');
            flux.emit(1.23F);
            flux.emit(',');
            flux.emit(1.23D);
            flux.emit(',');
            flux.emit(123.456F);
            flux.emit(',');
            flux.emit(123.456D);
            flux.emit(',');
            flux.emit(-123.456F);
            flux.emit(',');
            flux.emit(-123.456D);
            flux.emit(',');
            flux.emit((short) 0);
            flux.emit(',');
            flux.emit((short) 1);
            flux.emit(',');
            flux.emit((short) -1);
            flux.emit(',');
            flux.emit((short) 123);
            flux.emit(',');
            flux.emit((short) -123);
            flux.emit(',');
            flux.emit((short) 32767);
            flux.emit(',');
            flux.emit((short) -32768);
            flux.emit(',');
            flux.emit(0);
            flux.emit(',');
            flux.emit(1);
            flux.emit(',');
            flux.emit(-1);
            flux.emit(',');
            flux.emit(123);
            flux.emit(',');
            flux.emit(123456);
            flux.emit(',');
            flux.emit(-123);
            flux.emit(',');
            flux.emit(-123456);
            flux.emit(',');
            flux.emit(2147483647);
            flux.emit(',');
            flux.emit(-2147483648);
            flux.emit(',');
            flux.emit(0L);
            flux.emit(',');
            flux.emit(1L);
            flux.emit(',');
            flux.emit(-1L);
            flux.emit(',');
            flux.emit(123L);
            flux.emit(',');
            flux.emit(123456L);
            flux.emit(',');
            flux.emit(123456654321L);
            flux.emit(',');
            flux.emit(-123L);
            flux.emit(',');
            flux.emit(-123456L);
            flux.emit(',');
            flux.emit(-123456654321L);
            flux.emit(',');
            flux.emit(9223372036854775807L);
            flux.emit(',');
            flux.emit(-9223372036854775808L);
            assertEquals(
                "0.0,0.0,1.0,1.0,-1.0,-1.0,1.23,1.23,123.456,123.456,-123.456,-123.456,0,1,65535,123,65413,32767,32768," +
                    "0,1,-1,123,123456,-123,-123456,2147483647,-2147483648,0,1,-1,123,123456,123456654321," +
                    "-123,-123456,-123456654321,9223372036854775807,-9223372036854775808", flux.toString()
            );
        }
    }

    @Test
    public void test_string() throws IOException {
        try (Stream flux = new Stream() {
            {
                for (int i = 0; i < 3; i++) {
                    switch (i) {
                        case 1: {
                            state = 1;
                            break;
                        }
                        case 2: {
                            coding = true;
                            break;
                        }
                    }
                    emit(
                        "[{\"\0\1\2kraity\t\r\n\"}]"
                    );
                    emit(
                        "@#,:=L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏"
                    );
                }
            }
        }) {
            assertEquals(
                "[{\\\"\\u0000\\u0001\\u0002kraity\\t\\r\\n\\\"}]@#,:=L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏" +
                    "\\[\\{\\\"\\u0000\\u0001\\u0002kraity\\t\\r\\n\\\"\\}\\]\\@\\#\\,\\:\\=L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏" +
                    "\\[\\{\\\"\\u0000\\u0001\\u0002kraity\\t\\r\\n\\\"\\}\\]\\@\\#\\,\\:\\=L" +
                    "\\u00A9\\u00B5\\u0141\\u018E\\u02AA\\u02E9\\u03A3\\u300E\\u9646\\u4E4B\\u5C87\\u300F\\uD83E\\uDDEC\\uD83C\\uDFF7\\u26F0\\uFE0F\\uD83C\\uDF0F", flux.toString()
            );
        }
    }
}
