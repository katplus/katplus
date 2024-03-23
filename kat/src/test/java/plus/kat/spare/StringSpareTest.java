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
            Flow.of(
                "\"@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏\""
            )
        );
        assertNotNull(sb);
        try (Chan chan = spare.write(sb, Flag.NORM)) {
            assertEquals("@String \"@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏\"", chan.toString());
        }
    }

}
