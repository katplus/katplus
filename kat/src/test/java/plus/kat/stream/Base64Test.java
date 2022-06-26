package plus.kat.stream;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class Base64Test {

    public void assertTest(
        Base64 $,
        String a,
        String b
    ) {
        byte[] c = a.getBytes(UTF_8);
        assertEquals(
            b, new String($.encode(c))
        );
        assertArrayEquals(
            c, $.decode(b.getBytes())
        );
    }

    @Test
    public void test_code() {
        Base64 base = Base64.base();
        Base64 mime = Base64.mime();

        assertTest(base, "kraity", "a3JhaXR5");
        assertTest(base, "1", "MQ==");
        assertTest(base, "kat", "a2F0");
        assertTest(base, "数据交换格式", "5pWw5o2u5Lqk5o2i5qC85byP");

        assertTest(mime, "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWkFCQ0RF\r\n" +
                "RkdISUpLTE1OT1BRUlNUVVZXWFlaQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpBQkNERUZHSElK\r\n" +
                "S0xNTk9QUVJTVFVWV1hZWkFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaQUJDREVGR0hJSktMTU5P\r\n" +
                "UFFSU1RVVldYWVpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWg==");
        assertTest(mime, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234",
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0\r\n" +
                "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0");
    }

    @Test
    public void test_mime() {
        String data = "'0', '1', '2', '3', '4', '5'," +
            "'6', '7', '8', '9', 'A', 'B'," +
            "'C', 'D', 'E', 'F', 'G', 'H'," +
            "'I', 'J', 'K', 'L', 'M', 'N'," +
            "'O', 'P', 'Q', 'R', 'S', 'T'," +
            "'U', 'V', 'W', 'X', 'Y', 'Z'";

        String mine = "JzAnLCAnMScsICcyJywgJzMnLCAnNCcsICc1JywnNicsICc3JywgJzgnLCAnOScsICdBJywgJ0In\r\n" +
            "LCdDJywgJ0QnLCAnRScsICdGJywgJ0cnLCAnSCcsJ0knLCAnSicsICdLJywgJ0wnLCAnTScsICdO\r\n" +
            "JywnTycsICdQJywgJ1EnLCAnUicsICdTJywgJ1QnLCdVJywgJ1YnLCAnVycsICdYJywgJ1knLCAn\r\n" +
            "Wic=";

        Base64 base = Base64.mime();

        assertEquals(
            mine, new String(
                base.encode(
                    data.getBytes(UTF_8)
                )
            )
        );
        assertEquals(
            data, new String(
                base.decode(
                    mine.getBytes(UTF_8)
                )
            )
        );
    }
}
