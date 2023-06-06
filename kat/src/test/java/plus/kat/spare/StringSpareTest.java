package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class StringSpareTest {

    @Test
    public void test_base() throws IOException {
        Spare<String> spare = StringSpare.INSTANCE;

        String sb = spare.read(
            Flow.of("\"@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏\"")
        );
        assertNotNull(sb);
        try (Chan chan = spare.write(sb, Flag.NORM)) {
            assertEquals("@String \"@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏\"", chan.toString());
        }
    }

    @Test
    public void test_base_StringBuffer() throws IOException {
        Spare<StringBuffer> spare = StringBufferSpare.INSTANCE;

        StringBuffer sb = spare.read(
            Flow.of("\"@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏\"")
        );
        assertNotNull(sb);
        try (Chan chan = spare.write(sb, Flag.NORM)) {
            assertEquals("@String \"@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏\"", chan.toString());
        }
    }

    @Test
    public void test_base_StringBuilder() throws IOException {
        Spare<StringBuilder> spare = StringBuilderSpare.INSTANCE;

        StringBuilder sb = spare.read(
            Flow.of("\"@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏\"")
        );
        assertNotNull(sb);
        try (Chan chan = spare.write(sb, Flag.NORM)) {
            assertEquals("@String \"@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏\"", chan.toString());
        }
    }
}
