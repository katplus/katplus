package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flag;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class FileSpareTest {

    @Test
    public void test_base() throws IOException {
        Spare<File> spare = FileSpare.INSTANCE;

        File f0 = spare.read(
            Flow.of(
                "\"file:\\\\kat.plus\\\\user.kat\""
            )
        );

        try (Chan chan = spare.write(f0, Flag.NORM)) {
            assertEquals("@File \"file:\\\\kat.plus\\\\user.kat\"", chan.toString());
        }
    }
}
