package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.chain.*;
import plus.kat.flow.Stream;

import java.io.IOException;

import static plus.kat.spare.ByteArraySpare.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 */
public class ByteArraySpareTest {

    public void assertRead(
        String decoded,
        String encoded
    ) throws IOException {
        byte[] text = decoded.getBytes(UTF_8);
        byte[] data = encoded.getBytes(UTF_8);
        assertArrayEquals(
            text, INSTANCE.read(
                null, new Value(
                    data.length, data
                )
            )
        );
    }

    public void assertPass(
        String decoded,
        String encoded
    ) throws IOException {
        try (Stream flux = new Stream()) {
            byte[] text = decoded.getBytes(UTF_8);
            byte[] data = encoded.getBytes(UTF_8);

            INSTANCE.write(flux, text);
            assertArrayEquals(data, flux.toBinary());
            assertArrayEquals(
                text, INSTANCE.read(
                    null, new Value(
                        data.length, data
                    )
                )
            );
        }
    }

    @Test
    public void test_base() throws IOException {
        assertRead("0", "MA");
        assertRead("kat.plus", "a2F0LnBsdXM");

        assertPass("1", "MQ==");
        assertPass("kat", "a2F0");
        assertPass("kraity", "a3JhaXR5");
        assertPass("数据交换格式", "5pWw5o2u5Lqk5o2i5qC85byP");

        assertRead(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234",
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0\n" +
                "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0"
        );
        assertRead(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234",
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0\r\n" +
                "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0"
        );
        assertRead(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWkFCQ0RF\n" +
                "RkdISUpLTE1OT1BRUlNUVVZXWFlaQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpBQkNERUZHSElK\n" +
                "S0xNTk9QUVJTVFVWV1hZWkFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaQUJDREVGR0hJSktMTU5P\n" +
                "UFFSU1RVVldYWVpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWg=="
        );
        assertRead(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWkFCQ0RF\r\n" +
                "RkdISUpLTE1OT1BRUlNUVVZXWFlaQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpBQkNERUZHSElK\r\n" +
                "S0xNTk9QUVJTVFVWV1hZWkFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaQUJDREVGR0hJSktMTU5P\r\n" +
                "UFFSU1RVVldYWVpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWg=="
        );
    }
}
