package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flag;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class URISpareTest {

    @Test
    public void test_base() throws IOException {
        Spare<URI> spare = URISpare.INSTANCE;

        URI uri = spare.read(
            Flow.of(
                "\"https://kat.plus/user.kat\""
            )
        );

        assertNotNull(uri);
        try (Chan chan = spare.write(uri, Flag.NORM)) {
            assertEquals("@URI \"https://kat.plus/user.kat\"", chan.toString());
        }
    }
}
