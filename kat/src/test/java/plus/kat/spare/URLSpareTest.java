package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flag;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class URLSpareTest {

    @Test
    public void test_base() throws IOException {
        Spare<URL> spare = URLSpare.INSTANCE;

        URL url = spare.read(
            Flow.of(
                "\"https://kat.plus/user.kat\""
            )
        );

        assertNotNull(url);
        try (Chan chan = spare.write(url, Flag.NORM)) {
            assertEquals("@URL \"https://kat.plus/user.kat\"", chan.toString());
        }
    }
}
