package plus.kat;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static plus.kat.Pure.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class PureTest {

    @Test
    public void test() throws IOException {
        Entity entity = chan -> {
            chan.set("id", 1);
            chan.set("name", "kraity");
        };

        try (Chan chan = kat(entity)) {
            assertEquals("{id=1,name=\"kraity\"}", chan.toString());
        }
        try (Chan chan = doc(entity)) {
            assertEquals("<Map><id>1</id><name>kraity</name></Map>", chan.toString());
        }
        try (Chan chan = json(entity)) {
            assertEquals("{\"id\":1,\"name\":\"kraity\"}", chan.toString());
        }
    }
}
