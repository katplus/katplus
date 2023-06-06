package plus.kat.stream;

import org.junit.jupiter.api.Test;

import java.util.Random;

import plus.kat.chain.Value;

import static plus.kat.stream.Transfer.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class TransferTest {

    Random random = new Random();

    static Value value(
        Value ins, String value
    ) {
        int size = value.length();
        byte[] chunk = ins.flow();

        for (int i = 0; i < size; i++) {
            chunk[i] = (byte) value.charAt(i);
        }
        return ins.slip(size);
    }

    @Test
    public void test_toInt() {
        Value v = new Value(32);

        assertEquals(0, toInteger(value(v, "0"), null));
        assertEquals(0, toInteger(value(v, "0L"), null));
        assertEquals(0, toInteger(value(v, "0F"), null));
        assertEquals(0, toInteger(value(v, "0D"), null));

        assertEquals(1, toInteger(value(v, "1"), null));
        assertEquals(1, toInteger(value(v, "1."), null));
        assertEquals(1, toInteger(value(v, "1L"), null));
        assertEquals(1, toInteger(value(v, "1F"), null));
        assertEquals(1, toInteger(value(v, "1D"), null));

        assertEquals(6, toInteger(value(v, "6"), null));
        assertEquals(6, toInteger(value(v, "6L"), null));
        assertEquals(6, toInteger(value(v, "6F"), null));
        assertEquals(6, toInteger(value(v, "6D"), null));

        assertNull(toInteger(value(v, ""), null));
        assertNull(toInteger(value(v, "null"), null));
        assertEquals(0, toInteger(value(v, "0.0"), null));
        assertEquals(0, toInteger(value(v, "0.00"), null));
        assertEquals(0, toInteger(value(v, "0x00"), null));
        assertEquals(0, toInteger(value(v, "0b00"), null));
        assertEquals(0, toInteger(value(v, "0000"), null));
        assertEquals(1, toInteger(value(v, "1.123"), null));
        assertEquals(123, toInteger(value(v, "123.456"), null));
        assertEquals(123, toInteger(value(v, "123.456D"), null));
        assertEquals(-123, toInteger(value(v, "-123.456"), null));
        assertEquals(-123, toInteger(value(v, "-123.456D"), null));

        assertEquals(0, toInteger(value(v, ".0"), null));
        assertEquals(0, toInteger(value(v, "0."), null));
        assertEquals(0, toInteger(value(v, ".123"), null));
        assertEquals(1, toInteger(value(v, "true"), null));
        assertEquals(0, toInteger(value(v, "false"), null));
        assertEquals(0, toInteger(value(v, "0.0D"), null));
        assertEquals(1, toInteger(value(v, "1.0D"), null));
        assertEquals(123, toInteger(value(v, "123.0D"), null));
        assertEquals(123, toInteger(value(v, "123.456D"), null));

        assertEquals(123456, toInteger(value(v, "123456"), null));
        assertEquals(123456, toInteger(value(v, "123456L"), null));
        assertEquals(123456, toInteger(value(v, "123456F"), null));
        assertEquals(123456, toInteger(value(v, "123456D"), null));

        assertEquals(0x0, toInteger(value(v, "0x0"), null));
        assertEquals(0x1, toInteger(value(v, "0x1"), null));
        assertEquals(0x8, toInteger(value(v, "0x8"), null));
        assertEquals(0x00, toInteger(value(v, "0x00"), null));
        assertEquals(0xFF, toInteger(value(v, "0xFF"), null));
        assertEquals(0x8F, toInteger(value(v, "0x8F"), null));
        assertEquals(0x1234, toInteger(value(v, "0x1234"), null));
        assertEquals(0x12345678, toInteger(value(v, "0x12345678"), null));

        for (int i = 0; i < 64; i++) {
            int num = random.nextInt();
            assertEquals(
                num, toInteger(value(v, Integer.toString(num)), null)
            );
        }

        assertEquals(Integer.MAX_VALUE, toInteger(value(v, "2147483647"), null));
        assertEquals(Integer.MAX_VALUE, toInteger(value(v, "2147483647L"), null));
        assertEquals(Integer.MAX_VALUE, toInteger(value(v, "2147483647F"), null));
        assertEquals(Integer.MAX_VALUE, toInteger(value(v, "2147483647D"), null));

        assertEquals(Integer.MIN_VALUE, toInteger(value(v, "-2147483648"), null));
        assertEquals(Integer.MIN_VALUE, toInteger(value(v, "-2147483648L"), null));
        assertEquals(Integer.MIN_VALUE, toInteger(value(v, "-2147483648F"), null));
        assertEquals(Integer.MIN_VALUE, toInteger(value(v, "-2147483648D"), null));

        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "."), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "-"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "+"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "-."), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "+."), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "0x"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "0B"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "+D"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "00.00"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "01.00"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "01.23"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "0123.4567"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "0x123.0000"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "0x123.4567"), null));

        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "2147483648"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "-2147483649"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "12147483648"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "-12147483649"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "12345.567890L"), null));
        assertThrows(IllegalArgumentException.class, () -> toInteger(value(v, "-12345.567890L"), null));
    }

    @Test
    public void test_toLong() {
        Value v = new Value(32);

        assertEquals(0L, toLong(value(v, "0"), null));
        assertEquals(0L, toLong(value(v, "0L"), null));
        assertEquals(0L, toLong(value(v, "0F"), null));
        assertEquals(0L, toLong(value(v, "0D"), null));

        assertEquals(1L, toLong(value(v, "1"), null));
        assertEquals(1L, toLong(value(v, "1."), null));
        assertEquals(1L, toLong(value(v, "1L"), null));
        assertEquals(1L, toLong(value(v, "1F"), null));
        assertEquals(1L, toLong(value(v, "1D"), null));

        assertEquals(6L, toLong(value(v, "6"), null));
        assertEquals(6L, toLong(value(v, "6L"), null));
        assertEquals(6L, toLong(value(v, "6F"), null));
        assertEquals(6L, toLong(value(v, "6D"), null));

        assertNull(toLong(value(v, ""), null));
        assertNull(toLong(value(v, "null"), null));
        assertEquals(0L, toLong(value(v, "0.0"), null));
        assertEquals(0L, toLong(value(v, "0.00"), null));
        assertEquals(0L, toLong(value(v, "0x00"), null));
        assertEquals(0L, toLong(value(v, "0b00"), null));
        assertEquals(0L, toLong(value(v, "0000"), null));
        assertEquals(1L, toLong(value(v, "1.123"), null));
        assertEquals(123L, toLong(value(v, "123.456"), null));
        assertEquals(123L, toLong(value(v, "123.456D"), null));
        assertEquals(-123L, toLong(value(v, "-123.456"), null));
        assertEquals(-123L, toLong(value(v, "-123.456D"), null));

        assertEquals(0L, toLong(value(v, ".0"), null));
        assertEquals(0L, toLong(value(v, "0."), null));
        assertEquals(0L, toLong(value(v, ".123"), null));
        assertEquals(1L, toLong(value(v, "true"), null));
        assertEquals(0L, toLong(value(v, "false"), null));
        assertEquals(0L, toLong(value(v, "0.0D"), null));
        assertEquals(1L, toLong(value(v, "1.0D"), null));
        assertEquals(123L, toLong(value(v, "123.0D"), null));
        assertEquals(123L, toLong(value(v, "123.456D"), null));

        assertEquals(123456L, toLong(value(v, "123456"), null));
        assertEquals(123456L, toLong(value(v, "123456L"), null));
        assertEquals(123456L, toLong(value(v, "123456F"), null));
        assertEquals(123456L, toLong(value(v, "123456D"), null));

        assertEquals(0x0, toLong(value(v, "0x0"), null));
        assertEquals(0x1, toLong(value(v, "0x1"), null));
        assertEquals(0x8, toLong(value(v, "0x8"), null));
        assertEquals(0x00, toLong(value(v, "0x00"), null));
        assertEquals(0xFF, toLong(value(v, "0xFF"), null));
        assertEquals(0x8F, toLong(value(v, "0x8F"), null));
        assertEquals(0x1234, toLong(value(v, "0x1234"), null));
        assertEquals(0x12345678, toLong(value(v, "0x12345678"), null));

        for (int i = 0; i < 64; i++) {
            int num = random.nextInt();
            assertEquals(
                num, toLong(value(v, Integer.toString(num)), null)
            );
            long number = ((long) num << 32) + random.nextInt();
            assertEquals(
                number, toLong(value(v, Long.toString(number)), null)
            );
        }

        assertEquals(Long.MAX_VALUE, toLong(value(v, "9223372036854775807"), null));
        assertEquals(Long.MAX_VALUE, toLong(value(v, "9223372036854775807L"), null));
        assertEquals(Long.MAX_VALUE, toLong(value(v, "9223372036854775807F"), null));
        assertEquals(Long.MAX_VALUE, toLong(value(v, "9223372036854775807D"), null));

        assertEquals(Long.MIN_VALUE, toLong(value(v, "-9223372036854775808"), null));
        assertEquals(Long.MIN_VALUE, toLong(value(v, "-9223372036854775808L"), null));
        assertEquals(Long.MIN_VALUE, toLong(value(v, "-9223372036854775808F"), null));
        assertEquals(Long.MIN_VALUE, toLong(value(v, "-9223372036854775808D"), null));

        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "."), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "-"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "+"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "-."), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "0x"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "0b"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "+."), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "+D"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "00.00"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "01.00"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "01.23"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "0123.4567"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "0x123.0000"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "0x123.4567"), null));

        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "9223372036854775808"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "-9223372036854775809"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "19223372036854775807"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "-19223372036854775809"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "1234567890.1234567890L"), null));
        assertThrows(IllegalArgumentException.class, () -> toLong(value(v, "-1234567890.1234567890L"), null));
    }

    @Test
    public void test_toFloat() {
        Value v = new Value(32);

        assertNull(toFloat(value(v, ""), null));
        assertNull(toFloat(value(v, "null"), null));

        assertEquals(0F, toFloat(value(v, "0"), null));
        assertEquals(1F, toFloat(value(v, "1"), null));
        assertEquals(1F, toFloat(value(v, "true"), null));
        assertEquals(0F, toFloat(value(v, "false"), null));
        assertEquals(1.23F, toFloat(value(v, "1.23"), null));
        assertEquals(123.456F, toFloat(value(v, "123.456"), null));
    }

    @Test
    public void test_toDouble() {
        Value v = new Value(32);

        assertNull(toDouble(value(v, ""), null));
        assertNull(toDouble(value(v, "null"), null));

        assertEquals(0D, toDouble(value(v, "0"), null));
        assertEquals(1D, toDouble(value(v, "1"), null));
        assertEquals(1D, toDouble(value(v, "true"), null));
        assertEquals(0D, toDouble(value(v, "false"), null));
        assertEquals(1.23D, toDouble(value(v, "1.23"), null));
        assertEquals(123.456D, toDouble(value(v, "123.456"), null));
    }

    @Test
    public void test_toNumber() {
        Value v = new Value(64);

        assertEquals(0, toNumber(value(v, "0"), null));
        assertEquals(0L, toNumber(value(v, "0L"), null));
        assertEquals(0F, toNumber(value(v, "0F"), null));
        assertEquals(0D, toNumber(value(v, "0D"), null));

        assertEquals(1, toNumber(value(v, "1"), null));
        assertEquals(1D, toNumber(value(v, "1."), null));
        assertEquals(1L, toNumber(value(v, "1L"), null));
        assertEquals(1F, toNumber(value(v, "1F"), null));
        assertEquals(1D, toNumber(value(v, "1D"), null));

        assertEquals(6, toNumber(value(v, "6"), null));
        assertEquals(6L, toNumber(value(v, "6L"), null));
        assertEquals(6F, toNumber(value(v, "6F"), null));
        assertEquals(6d, toNumber(value(v, "6D"), null));

        assertEquals(0.D, toNumber(value(v, "0."), null));
        assertEquals(.0D, toNumber(value(v, ".0"), null));
        assertEquals(0.D, toNumber(value(v, "0.D"), null));
        assertEquals(1.D, toNumber(value(v, "1.D"), null));
        assertEquals(0, toNumber(value(v, "0x00"), null));
        assertEquals(0, toNumber(value(v, "0b00"), null));
        assertEquals(0, toNumber(value(v, "0000"), null));
        assertEquals(.123D, toNumber(value(v, ".123"), null));
        assertEquals(.123F, toNumber(value(v, ".123F"), null));
        assertEquals(0D, toNumber(value(v, "0.0"), null));
        assertEquals(1.123D, toNumber(value(v, "1.123"), null));
        assertNull(toNumber(value(v, "123.456L"), null));
        assertEquals(123.456D, toNumber(value(v, "123.456"), null));

        assertNull(toNumber(value(v, "0x"), null));
        assertNull(toNumber(value(v, "0b"), null));
        assertNull(toNumber(value(v, "01."), null));
        assertNull(toNumber(value(v, "null"), null));
        assertNull(toNumber(value(v, "00.00"), null));
        assertNull(toNumber(value(v, "01.00"), null));
        assertNull(toNumber(value(v, "01.23"), null));
        assertNull(toNumber(value(v, "0123.4567"), null));
        assertNull(toNumber(value(v, "0x123.0000"), null));
        assertNull(toNumber(value(v, "0x1234.45678"), null));

        assertEquals(1, toNumber(value(v, "true"), null));
        assertEquals(0, toNumber(value(v, "false"), null));
        assertEquals(0D, toNumber(value(v, "0.0D"), null));
        assertEquals(1D, toNumber(value(v, "1.0D"), null));
        assertEquals(1D, toNumber(value(v, "1.000"), null));
        assertEquals(123.0D, toNumber(value(v, "123.0D"), null));
        assertNull(toNumber(value(v, "123.456L"), null));
        assertEquals(123.456F, toNumber(value(v, "123.456F"), null));
        assertEquals(123.456D, toNumber(value(v, "123.456D"), null));
        assertEquals(-123.456F, toNumber(value(v, "-123.456F"), null));
        assertEquals(-123.456D, toNumber(value(v, "-123.456D"), null));

        assertEquals(123456, toNumber(value(v, "123456"), null));
        assertEquals(123456L, toNumber(value(v, "123456L"), null));
        assertEquals(123456F, toNumber(value(v, "123456F"), null));
        assertEquals(123456D, toNumber(value(v, "123456D"), null));

        assertEquals(0x0, toNumber(value(v, "0x0"), null));
        assertEquals(0x1, toNumber(value(v, "0x1"), null));
        assertEquals(0x8, toNumber(value(v, "0x8"), null));
        assertEquals(0x00, toNumber(value(v, "0x00"), null));
        assertEquals(0xFF, toNumber(value(v, "0xFF"), null));
        assertEquals(0x8F, toNumber(value(v, "0x8F"), null));
        assertEquals(0x1234, toNumber(value(v, "0x1234"), null));
        assertEquals(0x12345678, toNumber(value(v, "0x12345678"), null));
        assertEquals(0x80000000L, toNumber(value(v, "0x80000000"), null));
        assertEquals(0x12345678L, toNumber(value(v, "0x12345678L"), null));

        assertEquals(2147483648L, toNumber(value(v, "2147483648"), null));
        assertEquals(-2147483649L, toNumber(value(v, "-2147483649"), null));
        assertEquals(12147483648L, toNumber(value(v, "12147483648"), null));
        assertEquals(-12147483649L, toNumber(value(v, "-12147483649"), null));

        assertNull(toNumber(value(v, "."), null));
        assertNull(toNumber(value(v, "-"), null));
        assertNull(toNumber(value(v, "+"), null));
        assertNull(toNumber(value(v, "-."), null));
        assertNull(toNumber(value(v, "+."), null));
        assertNull(toNumber(value(v, "+D"), null));
        assertNull(toNumber(value(v, "9223372036854775808"), null));
        assertNull(toNumber(value(v, "-9223372036854775809"), null));
        assertNull(toNumber(value(v, "19223372036854775807"), null));
        assertNull(toNumber(value(v, "-19223372036854775809"), null));

        for (int i = 0; i < 64; i++) {
            int num = random.nextInt();
            assertEquals(
                num, toNumber(value(v, Integer.toString(num)), null)
            );
            long number = ((long) num << 32) + random.nextInt();
            assertEquals(
                number, toNumber(value(v, Long.toString(number)), null)
            );
        }

        assertEquals(2147483647, toNumber(value(v, "2147483647"), null));
        assertEquals(2147483647L, toNumber(value(v, "2147483647L"), null));
        assertEquals(2147483647F, toNumber(value(v, "2147483647F"), null));
        assertEquals(2147483647D, toNumber(value(v, "2147483647D"), null));

        assertEquals(-2147483648, toNumber(value(v, "-2147483648"), null));
        assertEquals(-2147483648L, toNumber(value(v, "-2147483648L"), null));
        assertEquals(-2147483648F, toNumber(value(v, "-2147483648F"), null));
        assertEquals(-2147483648D, toNumber(value(v, "-2147483648D"), null));

        assertEquals(9223372036854775807L, toNumber(value(v, "9223372036854775807"), null));
        assertEquals(9223372036854775807L, toNumber(value(v, "9223372036854775807L"), null));
        assertEquals(9223372036854775807F, toNumber(value(v, "9223372036854775807F"), null));
        assertEquals(9223372036854775807D, toNumber(value(v, "9223372036854775807D"), null));

        assertEquals(-9223372036854775808L, toNumber(value(v, "-9223372036854775808"), null));
        assertEquals(-9223372036854775808L, toNumber(value(v, "-9223372036854775808L"), null));
        assertEquals(-9223372036854775808F, toNumber(value(v, "-9223372036854775808F"), null));
        assertEquals(-9223372036854775808D, toNumber(value(v, "-9223372036854775808D"), null));
    }

    @Test
    public void test_toBoolean() {
        Value v = new Value(32);

        assertTrue(toBoolean(value(v, "1"), null));
        assertTrue(toBoolean(value(v, "1L"), null));
        assertTrue(toBoolean(value(v, "1F"), null));
        assertTrue(toBoolean(value(v, "1D"), null));
        assertTrue(toBoolean(value(v, "0.1"), null));
        assertTrue(toBoolean(value(v, "1.1"), null));
        assertTrue(toBoolean(value(v, "1.0"), null));
        assertTrue(toBoolean(value(v, "true"), null));
        assertTrue(toBoolean(value(v, "True"), null));
        assertTrue(toBoolean(value(v, "TRUE"), null));
        assertTrue(toBoolean(value(v, ".123"), null));
        assertTrue(toBoolean(value(v, "0.123"), null));
        assertTrue(toBoolean(value(v, "01.00"), null));
        assertTrue(toBoolean(value(v, "01.23"), null));
        assertTrue(toBoolean(value(v, "123.000"), null));
        assertTrue(toBoolean(value(v, "123.456"), null));
        assertTrue(toBoolean(value(v, "-123.000"), null));
        assertTrue(toBoolean(value(v, "-123.456"), null));

        assertTrue(toBoolean(value(v, "2147483647"), null));
        assertTrue(toBoolean(value(v, "2147483647L"), null));
        assertTrue(toBoolean(value(v, "2147483647F"), null));
        assertTrue(toBoolean(value(v, "2147483647D"), null));

        assertTrue(toBoolean(value(v, "-2147483648"), null));
        assertTrue(toBoolean(value(v, "-2147483648L"), null));
        assertTrue(toBoolean(value(v, "-2147483648F"), null));
        assertTrue(toBoolean(value(v, "-2147483648D"), null));

        assertTrue(toBoolean(value(v, "9223372036854775807"), null));
        assertTrue(toBoolean(value(v, "9223372036854775807L"), null));
        assertTrue(toBoolean(value(v, "9223372036854775807F"), null));
        assertTrue(toBoolean(value(v, "9223372036854775807D"), null));

        assertTrue(toBoolean(value(v, "-9223372036854775808"), null));
        assertTrue(toBoolean(value(v, "-9223372036854775808L"), null));
        assertTrue(toBoolean(value(v, "-9223372036854775808F"), null));
        assertTrue(toBoolean(value(v, "-9223372036854775808D"), null));

        assertFalse(toBoolean(value(v, "0"), null));
        assertFalse(toBoolean(value(v, "0L"), null));
        assertFalse(toBoolean(value(v, "0F"), null));
        assertFalse(toBoolean(value(v, "0D"), null));
        assertFalse(toBoolean(value(v, ".0"), null));
        assertFalse(toBoolean(value(v, "0."), null));
        assertFalse(toBoolean(value(v, "0.D"), null));
        assertFalse(toBoolean(value(v, "0.0"), null));
        assertFalse(toBoolean(value(v, "0x0"), null));
        assertFalse(toBoolean(value(v, "0x00"), null));
        assertFalse(toBoolean(value(v, "0b00"), null));
        assertFalse(toBoolean(value(v, "0.00"), null));
        assertFalse(toBoolean(value(v, "false"), null));
        assertFalse(toBoolean(value(v, "False"), null));
        assertFalse(toBoolean(value(v, "FALSE"), null));
        assertFalse(toBoolean(value(v, "00.00"), null));
        assertFalse(toBoolean(value(v, "00000000"), null));

        assertNull(toBoolean(value(v, ""), null));
        assertNull(toBoolean(value(v, "null"), null));

        assertTrue(toBoolean(value(v, "0x1"), null));
        assertTrue(toBoolean(value(v, "0x8"), null));
        assertTrue(toBoolean(value(v, "0xFF"), null));
        assertTrue(toBoolean(value(v, "0x8F"), null));
        assertTrue(toBoolean(value(v, "0x1234"), null));
        assertTrue(toBoolean(value(v, "0x12345678"), null));
        assertTrue(toBoolean(value(v, "0x80000000"), null));
        assertTrue(toBoolean(value(v, "0x12345678L"), null));

        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "NaN"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "FALse"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "Infinity"), null));

        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "."), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "-"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "+"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "-."), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "0x"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "0b"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "+."), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "+D"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "0x123.0000"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "0x123.4567"), null));

        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "-9223372036854775808+"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "-9223372036854775808-"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "-9223372036854775808A"), null));
        assertThrows(IllegalArgumentException.class, () -> toBoolean(value(v, "-9223372036854775808B"), null));
    }
}
