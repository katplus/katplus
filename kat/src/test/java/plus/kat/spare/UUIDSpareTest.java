package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class UUIDSpareTest {

    @Test
    public void test_base() throws IOException {
        Spare<UUID> spare
            = UUIDSpare.INSTANCE;

        UUID id = spare.read(
            Flow.of(
                "092f7929-d2d6-44d6-9cc1-694c2e360c56"
            )
        );

        assertEquals("092f7929-d2d6-44d6-9cc1-694c2e360c56", id.toString());
        try (Chan chan = spare.write(id, Flag.NORM)) {
            assertEquals("@UUID \"092f7929-d2d6-44d6-9cc1-694c2e360c56\"", chan.toString());
        }
    }

    Flag flag = v -> false;
    Value value = new Value(64);
    Spare<UUID> spare = UUIDSpare.INSTANCE;

    @SuppressWarnings("deprecation")
    public void assertTest(
        String reader, String writer
    ) throws IOException {
        int size = reader.length();
        reader.getBytes(0, size, value.flow(), 0);

        UUID uuid = spare.read(
            flag, value.slip(
                size, (byte) '"'
            )
        );
        try (Chan chan = spare.write(uuid)) {
            assertEquals(writer, chan.toString());
        }
    }

    @Test
    public void test_read_and_write() throws IOException {
        for (int i = 0; i < 9; i++) {
            String uuid = UUID.randomUUID().toString();
            assertTest(uuid, '"' + uuid + '"');
            assertTest(uuid.replace("-", ""), '"' + uuid + '"');
        }
        assertTest("793bb86ef4044a4daa34ef6c4a4bb3ab", "\"793bb86e-f404-4a4d-aa34-ef6c4a4bb3ab\"");
        assertTest("17679749-9e14-47f6-99b6-0b3b85fe4ebd", "\"17679749-9e14-47f6-99b6-0b3b85fe4ebd\"");
    }
}
