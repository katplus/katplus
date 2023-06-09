package plus.kat.stream;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class StreamTest {

    @Test
    public void test_close() throws IOException {
        Stream stream = new Stream();
        assertNotNull(stream.bucket);

        stream.emit(1);
        stream.emit(',');
        stream.emit("kraity");
        assertEquals(
            "1,kraity", stream.toString()
        );

        stream.close();
        assertEquals(0, stream.size());
        assertEquals(0, stream.value.length);
    }
}
