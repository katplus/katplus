package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.chain.*;

import java.io.IOException;

import static plus.kat.spare.CharArraySpare.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 */
public class CharArraySpareTest {

    public void assertPass(
        String string
    ) throws IOException {
        char[] text = string.toCharArray();
        byte[] data = string.getBytes(UTF_8);
        assertArrayEquals(
            text, INSTANCE.read(
                null, new Value(
                    data.length, data
                )
            )
        );
    }

    @Test
    public void test_base() throws IOException {
        assertPass("kraity");
        assertPass("数据交换格式");
        assertPass("@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏");
    }
}
