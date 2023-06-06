package plus.kat;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SpanTest {

    @Test
    public void test() throws IOException {
        Entity entity = chan -> {
            chan.set("id", 1);
            chan.set("name", "kraity");
        };

        assertEquals("{id=1,name=\"kraity\"}", Span.kat(entity));
        assertEquals("{\"id\":1,\"name\":\"kraity\"}", Span.json(entity));
        assertEquals("<User><id>1</id><name>kraity</name></User>", Span.doc("User", entity));
    }
}
