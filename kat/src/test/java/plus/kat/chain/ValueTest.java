package plus.kat.chain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValueTest {

    @Test
    public void test_toInt() {
        Value v1 = new Value("1400");
        assertEquals(1400, v1.toInt());

        Value v2 = new Value("2147483647");
        assertEquals(Integer.MAX_VALUE, v2.toInt());


        Value v3 = new Value("-2147483648");
        assertEquals(Integer.MIN_VALUE, v3.toInt());
    }

    @Test
    public void test_toLong() {
        Value v1 = new Value("1400");
        assertEquals(1400L, v1.toLong());

        Value v2 = new Value("9223372036854775807");
        assertEquals(Long.MAX_VALUE, v2.toLong());


        Value v3 = new Value("-9223372036854775808");
        assertEquals(Long.MIN_VALUE, v3.toLong());
    }

    @Test
    public void test_toFloat() {
        Value v1 = new Value("0x3F9D70A4");
        assertEquals(1.23F, v1.toFloat());

        Value v2 = new Value("0x42F6E979");
        assertEquals(123.456F, v2.toFloat());

        Value v3 = new Value("1.23");
        assertEquals(1.23F, v3.toFloat());

        Value v4 = new Value("123.456");
        assertEquals(123.456F, v4.toFloat());
    }

    @Test
    public void test_toDouble() {
        Value v1 = new Value("0x40934AB7318FC505");
        assertEquals(1234.6789D, v1.toDouble());

        Value v2 = new Value("0x405EDD3C07EE0B0B");
        assertEquals(123.456789D, v2.toDouble());

        Value v3 = new Value("1234.6789");
        assertEquals(1234.6789D, v3.toDouble());

        Value v4 = new Value("123.456789");
        assertEquals(123.456789D, v4.toDouble());
    }
}
