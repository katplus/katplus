package plus.kat.chain;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ValueTest {

    Random random = new Random();

    @SuppressWarnings("deprecation")
    static Value value(
        Value ins, String value
    ) {
        int size = value.length();
        value.getBytes(0, size, ins.flow(), 0);
        return ins.slip(size);
    }

    @Test
    public void test_isNothing() {
        Value v = new Value(16);

        assertTrue(value(v, "").isNothing());
        assertTrue(value(v, " ").isNothing());
        assertTrue(value(v, "\n").isNothing());
        assertTrue(value(v, "  ").isNothing());
        assertTrue(value(v, "null").isNothing());
        assertTrue(value(v, " \t ").isNothing());
        assertTrue(value(v, " null ").isNothing());
        assertTrue(value(v, "\nnull\n").isNothing());
        assertTrue(value(v, "\n\tnull\n\t").isNothing());

        assertFalse(value(v, "0").isNothing());
        assertFalse(value(v, "1").isNothing());
        assertFalse(value(v, " 1 ").isNothing());
        assertFalse(value(v, "\nkat\n").isNothing());
        assertFalse(value(v, "NULL").isNothing());
        assertFalse(value(v, "nullnull").isNothing());
        assertFalse(value(v, "nullable").isNothing());
        assertFalse(value(v, " nullable ").isNothing());
        assertFalse(value(v, "\nnullable\n").isNothing());
        assertFalse(value(v, "\nnull is void\n").isNothing());
    }

    @Test
    public void test_isAnything() {
        Value v = new Value(16);

        assertTrue(value(v, "0").isAnything());
        assertTrue(value(v, "1").isAnything());
        assertTrue(value(v, " 1 ").isAnything());
        assertTrue(value(v, "\nkat\n").isAnything());
        assertTrue(value(v, "NULL").isAnything());
        assertTrue(value(v, "nullnull").isAnything());
        assertTrue(value(v, "nullable").isAnything());
        assertTrue(value(v, " nullable ").isAnything());
        assertTrue(value(v, "\nnullable\n").isAnything());
        assertTrue(value(v, "\nnull is void\n").isAnything());

        assertFalse(value(v, "").isAnything());
        assertFalse(value(v, " ").isAnything());
        assertFalse(value(v, "\n").isAnything());
        assertFalse(value(v, "  ").isAnything());
        assertFalse(value(v, "null").isAnything());
        assertFalse(value(v, " \t ").isAnything());
        assertFalse(value(v, " null ").isAnything());
        assertFalse(value(v, "\nnull\n").isAnything());
        assertFalse(value(v, "\n\tnull\n\t").isAnything());
    }

    @Test
    public void test_isDigits() {
        Value v = new Value(16);

        assertTrue(value(v, "0").isDigits());
        assertTrue(value(v, "1").isDigits());
        assertTrue(value(v, "123456").isDigits());

        assertFalse(value(v, "").isDigits());
        assertFalse(value(v, "").isDigits());
        assertFalse(value(v, "0.").isDigits());
        assertFalse(value(v, ".1").isDigits());
        assertFalse(value(v, "123.").isDigits());
        assertFalse(value(v, "null").isDigits());
        assertFalse(value(v, "true").isDigits());
        assertFalse(value(v, "0xFF").isDigits());
        assertFalse(value(v, "123.456").isDigits());
        assertFalse(value(v, "-123.456").isDigits());
        assertFalse(value(v, "-12345678").isDigits());
        assertFalse(value(v, "+12345678").isDigits());
    }

    @Test
    public void test_Number() {
        Value v = new Value(16);

        assertTrue(value(v, "0").isNumber());
        assertTrue(value(v, "1").isNumber());
        assertTrue(value(v, "123456").isNumber());
        assertTrue(value(v, "-123456").isNumber());
        assertTrue(value(v, "+123456").isNumber());
        assertTrue(value(v, "123456.789").isNumber());
        assertTrue(value(v, "-123456.789").isNumber());
        assertTrue(value(v, "+123456.789").isNumber());

        assertFalse(value(v, "").isNumber());
        assertFalse(value(v, "0.").isNumber());
        assertFalse(value(v, ".1").isNumber());
        assertFalse(value(v, "123.").isNumber());
        assertFalse(value(v, "null").isNumber());
        assertFalse(value(v, "true").isNumber());
        assertFalse(value(v, "NaN").isNumber());
        assertFalse(value(v, "0x32").isNumber());
        assertFalse(value(v, "0xFF").isNumber());
        assertFalse(value(v, "false").isNumber());
        assertFalse(value(v, "Infinity").isNumber());
        assertFalse(value(v, "123+456").isNumber());
        assertFalse(value(v, "1.23E3").isNumber());
        assertFalse(value(v, "12.3E+7").isNumber());
        assertFalse(value(v, "-1.23E-12").isNumber());
        assertFalse(value(v, "1234.5E-4").isNumber());
        assertFalse(value(v, "1234.56.78").isNumber());
        assertFalse(value(v, "-1234.56.78").isNumber());
    }

    @Test
    public void test_toInt() {
        Value v = new Value(32);

        assertEquals(0, value(v, "0").toInt());
        assertEquals(1, value(v, "1").toInt());
        assertEquals(6, value(v, "6").toInt());
        assertEquals(0, value(v, ".0").toInt());
        assertEquals(0, value(v, "0.").toInt());
        assertEquals(0, value(v, "0.0").toInt());
        assertEquals(0, value(v, "0.00").toInt());
        assertEquals(1, value(v, "1.00").toInt());
        assertEquals(0, value(v, "0000").toInt());
        assertEquals(0, value(v, ".123").toInt());
        assertEquals(1, value(v, "1.123").toInt());
        assertEquals(0, value(v, "00.00").toInt());
        assertEquals(1, value(v, "01.00").toInt());
        assertEquals(1, value(v, "01.23").toInt());
        assertEquals(123, value(v, "123.456").toInt());
        assertEquals(-123, value(v, "-123.456").toInt());
        assertEquals(123, value(v, "0123.4567").toInt());
        assertEquals(-123, value(v, "-0123.4567").toInt());
        assertEquals(Integer.MAX_VALUE, value(v, "2147483647").toInt());
        assertEquals(Integer.MIN_VALUE, value(v, "-2147483648").toInt());

        for (int i = 0; i < 64; i++) {
            int num = random.nextInt();
            assertEquals(
                num, value(v, Integer.toString(num)).toInt()
            );
        }

        assertThrows(NumberFormatException.class, () -> value(v, "0x6").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "null").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "true").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "false").toInt());

        assertThrows(NumberFormatException.class, () -> value(v, "").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, ".").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "-").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "+").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "-.").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "+.").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "0x").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "0B").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "+D").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "0L").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "1L").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "1F").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "1D").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "0.0F").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "1.0F").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "0.0D").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "1.0D").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "123L").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "123.0D").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "12.3E+7").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "123.456F").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "123.456D").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "0x123.0000").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "0x123.4567").toInt());

        assertThrows(NumberFormatException.class, () -> value(v, "2147483648").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "-2147483649").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "12147483648").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "-12147483649").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "12345.567890L").toInt());
        assertThrows(NumberFormatException.class, () -> value(v, "-12345.567890L").toInt());
    }

    @Test
    public void test_toInt_Def() {
        Value v = new Value(32);

        assertEquals(0, value(v, "0").toInt(null));
        assertEquals(0, value(v, "0L").toInt(null));
        assertEquals(0, value(v, "0F").toInt(null));
        assertEquals(0, value(v, "0D").toInt(null));

        assertEquals(1, value(v, "1").toInt(null));
        assertEquals(1, value(v, "1.").toInt(null));
        assertEquals(1, value(v, "1L").toInt(null));
        assertEquals(1, value(v, "1F").toInt(null));
        assertEquals(1, value(v, "1D").toInt(null));

        assertEquals(6, value(v, "6").toInt(null));
        assertEquals(6, value(v, "6L").toInt(null));
        assertEquals(6, value(v, "6F").toInt(null));
        assertEquals(6, value(v, "6D").toInt(null));

        assertNull(value(v, "").toInt(null));
        assertNull(value(v, "null").toInt(null));
        assertEquals(0, value(v, "0.0").toInt(null));
        assertEquals(0, value(v, "0.00").toInt(null));
        assertEquals(0, value(v, "0x00").toInt(null));
        assertEquals(0, value(v, "0b00").toInt(null));
        assertEquals(0, value(v, "0000").toInt(null));
        assertEquals(1, value(v, "1.123").toInt(null));
        assertEquals(123, value(v, "123.456").toInt(null));
        assertEquals(123, value(v, "123.456D").toInt(null));
        assertEquals(-123, value(v, "-123.456").toInt(null));
        assertEquals(-123, value(v, "-123.456D").toInt(null));

        assertEquals(0, value(v, ".0").toInt(null));
        assertEquals(0, value(v, "0.").toInt(null));
        assertEquals(0, value(v, ".123").toInt(null));
        assertEquals(1, value(v, "true").toInt(null));
        assertEquals(0, value(v, "false").toInt(null));
        assertEquals(0, value(v, "0.0D").toInt(null));
        assertEquals(1, value(v, "1.0D").toInt(null));
        assertEquals(123, value(v, "123.0D").toInt(null));
        assertEquals(123, value(v, "123.456D").toInt(null));

        assertEquals(123456, value(v, "123456").toInt(null));
        assertEquals(123456, value(v, "123456L").toInt(null));
        assertEquals(123456, value(v, "123456F").toInt(null));
        assertEquals(123456, value(v, "123456D").toInt(null));

        assertEquals(0x0, value(v, "0x0").toInt(null));
        assertEquals(0x1, value(v, "0x1").toInt(null));
        assertEquals(0x8, value(v, "0x8").toInt(null));
        assertEquals(0x00, value(v, "0x00").toInt(null));
        assertEquals(0xFF, value(v, "0xFF").toInt(null));
        assertEquals(0x8F, value(v, "0x8F").toInt(null));
        assertEquals(0x1234, value(v, "0x1234").toInt(null));
        assertEquals(0x12345678, value(v, "0x12345678").toInt(null));

        for (int i = 0; i < 64; i++) {
            int num = random.nextInt();
            assertEquals(
                num, value(v, Integer.toString(num)).toInt(null)
            );
        }

        assertEquals(Integer.MAX_VALUE, value(v, "2147483647").toInt(null));
        assertEquals(Integer.MAX_VALUE, value(v, "2147483647L").toInt(null));
        assertEquals(Integer.MAX_VALUE, value(v, "2147483647F").toInt(null));
        assertEquals(Integer.MAX_VALUE, value(v, "2147483647D").toInt(null));

        assertEquals(Integer.MIN_VALUE, value(v, "-2147483648").toInt(null));
        assertEquals(Integer.MIN_VALUE, value(v, "-2147483648L").toInt(null));
        assertEquals(Integer.MIN_VALUE, value(v, "-2147483648F").toInt(null));
        assertEquals(Integer.MIN_VALUE, value(v, "-2147483648D").toInt(null));

        assertThrows(IllegalArgumentException.class, () -> value(v, ".").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "+").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-.").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "+.").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0B").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "+D").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "00.00").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "01.00").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "01.23").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0123.4567").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x123.0000").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x123.4567").toInt(null));

        assertThrows(IllegalArgumentException.class, () -> value(v, "2147483648").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-2147483649").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "12147483648").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-12147483649").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "12345.567890L").toInt(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-12345.567890L").toInt(null));
    }

    @Test
    public void test_toLong() {
        Value v = new Value(32);

        assertEquals(0L, value(v, "0").toLong());
        assertEquals(1L, value(v, "1").toLong());
        assertEquals(6L, value(v, "6").toLong());
        assertEquals(0L, value(v, "0.0").toLong());
        assertEquals(0L, value(v, "0.00").toLong());
        assertEquals(0L, value(v, "0000").toLong());
        assertEquals(1L, value(v, "1.").toLong());
        assertEquals(0L, value(v, ".0").toLong());
        assertEquals(0L, value(v, "0.").toLong());
        assertEquals(0L, value(v, ".123").toLong());
        assertEquals(1L, value(v, "1.123").toLong());
        assertEquals(0L, value(v, "00.00").toLong());
        assertEquals(1L, value(v, "01.00").toLong());
        assertEquals(1L, value(v, "01.23").toLong());
        assertEquals(123L, value(v, "123").toLong());
        assertEquals(123L, value(v, "123.456").toLong());
        assertEquals(123L, value(v, "0123.4567").toLong());
        assertEquals(-123L, value(v, "-123.456").toLong());
        assertEquals(Long.MAX_VALUE, value(v, "9223372036854775807").toLong());
        assertEquals(Long.MIN_VALUE, value(v, "-9223372036854775808").toLong());

        for (int i = 0; i < 64; i++) {
            int num = random.nextInt();
            assertEquals(
                num, value(v, Integer.toString(num)).toLong()
            );
            long number = ((long) num << 32) + random.nextInt();
            assertEquals(
                number, value(v, Long.toString(number)).toLong()
            );
        }

        assertThrows(NumberFormatException.class, () -> value(v, "").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "0x00").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "0b00").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "null").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "true").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "false").toLong());

        assertThrows(NumberFormatException.class, () -> value(v, ".").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "-").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "+").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "-.").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "0x").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "0b").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "+.").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "+D").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "0L").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "1L").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "1F").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "1D").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "0.0F").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "1.0F").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "0.0D").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "1.0D").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "123L").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "123.0D").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "12.3E+7").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "123.456F").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "123.456D").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "0x123.000").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "0x123.4567").toLong());

        assertThrows(NumberFormatException.class, () -> value(v, "9223372036854775808").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "-9223372036854775809").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "19223372036854775807").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "-19223372036854775809").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "1234567890.1234567890L").toLong());
        assertThrows(NumberFormatException.class, () -> value(v, "-1234567890.1234567890L").toLong());
    }

    @Test
    public void test_toLong_Def() {
        Value v = new Value(32);

        assertEquals(0L, value(v, "0").toLong(null));
        assertEquals(0L, value(v, "0L").toLong(null));
        assertEquals(0L, value(v, "0F").toLong(null));
        assertEquals(0L, value(v, "0D").toLong(null));

        assertEquals(1L, value(v, "1").toLong(null));
        assertEquals(1L, value(v, "1.").toLong(null));
        assertEquals(1L, value(v, "1L").toLong(null));
        assertEquals(1L, value(v, "1F").toLong(null));
        assertEquals(1L, value(v, "1D").toLong(null));

        assertEquals(6L, value(v, "6").toLong(null));
        assertEquals(6L, value(v, "6L").toLong(null));
        assertEquals(6L, value(v, "6F").toLong(null));
        assertEquals(6L, value(v, "6D").toLong(null));

        assertNull(value(v, "").toLong(null));
        assertNull(value(v, "null").toLong(null));
        assertEquals(0L, value(v, "0.0").toLong(null));
        assertEquals(0L, value(v, "0.00").toLong(null));
        assertEquals(0L, value(v, "0x00").toLong(null));
        assertEquals(0L, value(v, "0b00").toLong(null));
        assertEquals(0L, value(v, "0000").toLong(null));
        assertEquals(1L, value(v, "1.123").toLong(null));
        assertEquals(123L, value(v, "123.456").toLong(null));
        assertEquals(123L, value(v, "123.456D").toLong(null));
        assertEquals(-123L, value(v, "-123.456").toLong(null));
        assertEquals(-123L, value(v, "-123.456D").toLong(null));

        assertEquals(0L, value(v, ".0").toLong(null));
        assertEquals(0L, value(v, "0.").toLong(null));
        assertEquals(0L, value(v, ".123").toLong(null));
        assertEquals(1L, value(v, "true").toLong(null));
        assertEquals(0L, value(v, "false").toLong(null));
        assertEquals(0L, value(v, "0.0D").toLong(null));
        assertEquals(1L, value(v, "1.0D").toLong(null));
        assertEquals(123L, value(v, "123.0D").toLong(null));
        assertEquals(123L, value(v, "123.456D").toLong(null));

        assertEquals(123456L, value(v, "123456").toLong(null));
        assertEquals(123456L, value(v, "123456L").toLong(null));
        assertEquals(123456L, value(v, "123456F").toLong(null));
        assertEquals(123456L, value(v, "123456D").toLong(null));

        assertEquals(0x0, value(v, "0x0").toLong(null));
        assertEquals(0x1, value(v, "0x1").toLong(null));
        assertEquals(0x8, value(v, "0x8").toLong(null));
        assertEquals(0x00, value(v, "0x00").toLong(null));
        assertEquals(0xFF, value(v, "0xFF").toLong(null));
        assertEquals(0x8F, value(v, "0x8F").toLong(null));
        assertEquals(0x1234, value(v, "0x1234").toLong(null));
        assertEquals(0x12345678, value(v, "0x12345678").toLong(null));

        for (int i = 0; i < 64; i++) {
            int num = random.nextInt();
            assertEquals(
                num, value(v, Integer.toString(num)).toLong(null)
            );
            long number = ((long) num << 32) + random.nextInt();
            assertEquals(
                number, value(v, Long.toString(number)).toLong(null)
            );
        }

        assertEquals(Long.MAX_VALUE, value(v, "9223372036854775807").toLong(null));
        assertEquals(Long.MAX_VALUE, value(v, "9223372036854775807L").toLong(null));
        assertEquals(Long.MAX_VALUE, value(v, "9223372036854775807F").toLong(null));
        assertEquals(Long.MAX_VALUE, value(v, "9223372036854775807D").toLong(null));

        assertEquals(Long.MIN_VALUE, value(v, "-9223372036854775808").toLong(null));
        assertEquals(Long.MIN_VALUE, value(v, "-9223372036854775808L").toLong(null));
        assertEquals(Long.MIN_VALUE, value(v, "-9223372036854775808F").toLong(null));
        assertEquals(Long.MIN_VALUE, value(v, "-9223372036854775808D").toLong(null));

        assertThrows(IllegalArgumentException.class, () -> value(v, ".").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "+").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-.").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0b").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "+.").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "+D").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "00.00").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "01.00").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "01.23").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0123.4567").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x123.0000").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x123.4567").toLong(null));

        assertThrows(IllegalArgumentException.class, () -> value(v, "9223372036854775808").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-9223372036854775809").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "19223372036854775807").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-19223372036854775809").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "1234567890.1234567890L").toLong(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-1234567890.1234567890L").toLong(null));
    }

    @Test
    public void test_toFloat() {
        Value v = new Value(32);

        assertEquals(0F, value(v, "0").toFloat());
        assertEquals(1F, value(v, "1").toFloat());
        assertEquals(1.23F, value(v, "1.23").toFloat());
        assertEquals(123.456F, value(v, "123.456").toFloat());
        assertEquals(123.456F, value(v, "+123.456").toFloat());
        assertEquals(-123.456F, value(v, "-123.456").toFloat());

        assertThrows(NumberFormatException.class, () -> value(v, "").toFloat());
        assertThrows(NumberFormatException.class, () -> value(v, "0.").toFloat());
        assertThrows(NumberFormatException.class, () -> value(v, ".0").toFloat());
        assertThrows(NumberFormatException.class, () -> value(v, "1.").toFloat());
        assertThrows(NumberFormatException.class, () -> value(v, ".1").toFloat());
        assertThrows(NumberFormatException.class, () -> value(v, "null").toFloat());
        assertThrows(NumberFormatException.class, () -> value(v, "true").toFloat());
        assertThrows(NumberFormatException.class, () -> value(v, "false").toFloat());
    }

    @Test
    public void test_toFloat_Def() {
        Value v = new Value(32);

        assertNull(value(v, "").toFloat(null));
        assertNull(value(v, "null").toFloat(null));

        assertEquals(0F, value(v, "0").toFloat(null));
        assertEquals(1F, value(v, "1").toFloat(null));
        assertEquals(1F, value(v, "true").toFloat(null));
        assertEquals(0F, value(v, "false").toFloat(null));
        assertEquals(1.23F, value(v, "1.23").toFloat(null));
        assertEquals(123.456F, value(v, "123.456").toFloat(null));
    }

    @Test
    public void test_toDouble() {
        Value v = new Value(32);

        assertEquals(0D, value(v, "0").toDouble());
        assertEquals(1d, value(v, "1").toDouble());
        assertEquals(1.23D, value(v, "1.23").toDouble());
        assertEquals(123.456D, value(v, "123.456").toDouble());
        assertEquals(123.456D, value(v, "+123.456").toDouble());
        assertEquals(-123.456D, value(v, "-123.456").toDouble());

        assertThrows(NumberFormatException.class, () -> value(v, "").toDouble());
        assertThrows(NumberFormatException.class, () -> value(v, "0.").toDouble());
        assertThrows(NumberFormatException.class, () -> value(v, ".0").toDouble());
        assertThrows(NumberFormatException.class, () -> value(v, "1.").toDouble());
        assertThrows(NumberFormatException.class, () -> value(v, ".1").toDouble());
        assertThrows(NumberFormatException.class, () -> value(v, "null").toDouble());
        assertThrows(NumberFormatException.class, () -> value(v, "true").toDouble());
        assertThrows(NumberFormatException.class, () -> value(v, "false").toDouble());
    }

    @Test
    public void test_toDouble_Def() {
        Value v = new Value(32);

        assertNull(value(v, "").toDouble(null));
        assertNull(value(v, "null").toDouble(null));

        assertEquals(0D, value(v, "0").toDouble(null));
        assertEquals(1D, value(v, "1").toDouble(null));
        assertEquals(1D, value(v, "true").toDouble(null));
        assertEquals(0D, value(v, "false").toDouble(null));
        assertEquals(1.23D, value(v, "1.23").toDouble(null));
        assertEquals(123.456D, value(v, "123.456").toDouble(null));
    }

    @Test
    public void test_toNumber_Def() {
        Value v = new Value(64);

        assertEquals(0, value(v, "0").toNumber(null));
        assertEquals(0L, value(v, "0L").toNumber(null));
        assertEquals(0F, value(v, "0F").toNumber(null));
        assertEquals(0D, value(v, "0D").toNumber(null));

        assertEquals(1, value(v, "1").toNumber(null));
        assertEquals(1D, value(v, "1.").toNumber(null));
        assertEquals(1L, value(v, "1L").toNumber(null));
        assertEquals(1F, value(v, "1F").toNumber(null));
        assertEquals(1D, value(v, "1D").toNumber(null));

        assertEquals(6, value(v, "6").toNumber(null));
        assertEquals(6L, value(v, "6L").toNumber(null));
        assertEquals(6F, value(v, "6F").toNumber(null));
        assertEquals(6d, value(v, "6D").toNumber(null));

        assertEquals(0.D, value(v, "0.").toNumber(null));
        assertEquals(.0D, value(v, ".0").toNumber(null));
        assertEquals(0.D, value(v, "0.D").toNumber(null));
        assertEquals(1.D, value(v, "1.D").toNumber(null));
        assertEquals(0, value(v, "0x00").toNumber(null));
        assertEquals(0, value(v, "0b00").toNumber(null));
        assertEquals(0, value(v, "0000").toNumber(null));
        assertEquals(.123D, value(v, ".123").toNumber(null));
        assertEquals(.123F, value(v, ".123F").toNumber(null));
        assertEquals(0D, value(v, "0.0").toNumber(null));
        assertEquals(1.123D, value(v, "1.123").toNumber(null));
        assertNull(value(v, "123.456L").toNumber(null));
        assertEquals(123.456D, value(v, "123.456").toNumber(null));

        assertNull(value(v, "0x").toNumber(null));
        assertNull(value(v, "0b").toNumber(null));
        assertNull(value(v, "01.").toNumber(null));
        assertNull(value(v, "null").toNumber(null));
        assertNull(value(v, "00.00").toNumber(null));
        assertNull(value(v, "01.00").toNumber(null));
        assertNull(value(v, "01.23").toNumber(null));
        assertNull(value(v, "0123.4567").toNumber(null));
        assertNull(value(v, "0x123.0000").toNumber(null));
        assertNull(value(v, "0x1234.45678").toNumber(null));

        assertEquals(1, value(v, "true").toNumber(null));
        assertEquals(0, value(v, "false").toNumber(null));
        assertEquals(0D, value(v, "0.0D").toNumber(null));
        assertEquals(1D, value(v, "1.0D").toNumber(null));
        assertEquals(1D, value(v, "1.000").toNumber(null));
        assertEquals(123.0D, value(v, "123.0D").toNumber(null));
        assertNull(value(v, "123.456L").toNumber(null));
        assertEquals(123.456F, value(v, "123.456F").toNumber(null));
        assertEquals(123.456D, value(v, "123.456D").toNumber(null));
        assertEquals(-123.456F, value(v, "-123.456F").toNumber(null));
        assertEquals(-123.456D, value(v, "-123.456D").toNumber(null));

        assertEquals(123456, value(v, "123456").toNumber(null));
        assertEquals(123456L, value(v, "123456L").toNumber(null));
        assertEquals(123456F, value(v, "123456F").toNumber(null));
        assertEquals(123456D, value(v, "123456D").toNumber(null));

        assertEquals(0x0, value(v, "0x0").toNumber(null));
        assertEquals(0x1, value(v, "0x1").toNumber(null));
        assertEquals(0x8, value(v, "0x8").toNumber(null));
        assertEquals(0x00, value(v, "0x00").toNumber(null));
        assertEquals(0xFF, value(v, "0xFF").toNumber(null));
        assertEquals(0x8F, value(v, "0x8F").toNumber(null));
        assertEquals(0x1234, value(v, "0x1234").toNumber(null));
        assertEquals(0x12345678, value(v, "0x12345678").toNumber(null));
        assertEquals(0x80000000L, value(v, "0x80000000").toNumber(null));
        assertEquals(0x12345678L, value(v, "0x12345678L").toNumber(null));

        assertEquals(2147483648L, value(v, "2147483648").toNumber(null));
        assertEquals(-2147483649L, value(v, "-2147483649").toNumber(null));
        assertEquals(12147483648L, value(v, "12147483648").toNumber(null));
        assertEquals(-12147483649L, value(v, "-12147483649").toNumber(null));

        assertNull(value(v, ".").toNumber(null));
        assertNull(value(v, "-").toNumber(null));
        assertNull(value(v, "+").toNumber(null));
        assertNull(value(v, "-.").toNumber(null));
        assertNull(value(v, "+.").toNumber(null));
        assertNull(value(v, "+D").toNumber(null));
        assertNull(value(v, "9223372036854775808").toNumber(null));
        assertNull(value(v, "-9223372036854775809").toNumber(null));
        assertNull(value(v, "19223372036854775807").toNumber(null));
        assertNull(value(v, "-19223372036854775809").toNumber(null));

        for (int i = 0; i < 64; i++) {
            int num = random.nextInt();
            assertEquals(
                num, value(v, Integer.toString(num)).toNumber(null)
            );
            long number = ((long) num << 32) + random.nextInt();
            assertEquals(
                number, value(v, Long.toString(number)).toNumber(null)
            );
        }

        assertEquals(2147483647, value(v, "2147483647").toNumber(null));
        assertEquals(2147483647L, value(v, "2147483647L").toNumber(null));
        assertEquals(2147483647F, value(v, "2147483647F").toNumber(null));
        assertEquals(2147483647D, value(v, "2147483647D").toNumber(null));

        assertEquals(-2147483648, value(v, "-2147483648").toNumber(null));
        assertEquals(-2147483648L, value(v, "-2147483648L").toNumber(null));
        assertEquals(-2147483648F, value(v, "-2147483648F").toNumber(null));
        assertEquals(-2147483648D, value(v, "-2147483648D").toNumber(null));

        assertEquals(9223372036854775807L, value(v, "9223372036854775807").toNumber(null));
        assertEquals(9223372036854775807L, value(v, "9223372036854775807L").toNumber(null));
        assertEquals(9223372036854775807F, value(v, "9223372036854775807F").toNumber(null));
        assertEquals(9223372036854775807D, value(v, "9223372036854775807D").toNumber(null));

        assertEquals(-9223372036854775808L, value(v, "-9223372036854775808").toNumber(null));
        assertEquals(-9223372036854775808L, value(v, "-9223372036854775808L").toNumber(null));
        assertEquals(-9223372036854775808F, value(v, "-9223372036854775808F").toNumber(null));
        assertEquals(-9223372036854775808D, value(v, "-9223372036854775808D").toNumber(null));
    }

    @Test
    public void test_toBoolean() {
        Value v = new Value(32);

        assertTrue(value(v, "1").toBoolean());
        assertTrue(value(v, "12").toBoolean());
        assertTrue(value(v, "1.0").toBoolean());
        assertTrue(value(v, "1.2").toBoolean());
        assertTrue(value(v, "123").toBoolean());
        assertTrue(value(v, "true").toBoolean());
        assertTrue(value(v, "True").toBoolean());
        assertTrue(value(v, "TRUE").toBoolean());
        assertTrue(value(v, ".123").toBoolean());
        assertTrue(value(v, "0.123").toBoolean());

        assertFalse(value(v, "0").toBoolean());
        assertFalse(value(v, "-0").toBoolean());
        assertFalse(value(v, "+0").toBoolean());
        assertFalse(value(v, "0.0").toBoolean());
        assertFalse(value(v, "false").toBoolean());
        assertFalse(value(v, "False").toBoolean());
        assertFalse(value(v, "FALSE").toBoolean());

        assertThrows(IllegalArgumentException.class, () -> value(v, "").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, ".").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "NaN").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "null").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "FALse").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x32").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "1.2D").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "1.2F").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "123L").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "12.3E+7").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "Infinity").toBoolean());

        assertThrows(IllegalArgumentException.class, () -> value(v, "-9223372036854775808+").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "-9223372036854775808-").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "-9223372036854775808A").toBoolean());
        assertThrows(IllegalArgumentException.class, () -> value(v, "-9223372036854775808B").toBoolean());
    }

    @Test
    public void test_toBoolean_Def() {
        Value v = new Value(32);

        assertTrue(value(v, "1").toBoolean(null));
        assertTrue(value(v, "1L").toBoolean(null));
        assertTrue(value(v, "1F").toBoolean(null));
        assertTrue(value(v, "1D").toBoolean(null));
        assertTrue(value(v, "0.1").toBoolean(null));
        assertTrue(value(v, "1.1").toBoolean(null));
        assertTrue(value(v, "1.0").toBoolean(null));
        assertTrue(value(v, "true").toBoolean(null));
        assertTrue(value(v, "True").toBoolean(null));
        assertTrue(value(v, "TRUE").toBoolean(null));
        assertTrue(value(v, ".123").toBoolean(null));
        assertTrue(value(v, "0.123").toBoolean(null));
        assertTrue(value(v, "01.00").toBoolean(null));
        assertTrue(value(v, "01.23").toBoolean(null));
        assertTrue(value(v, "123.000").toBoolean(null));
        assertTrue(value(v, "123.456").toBoolean(null));
        assertTrue(value(v, "-123.000").toBoolean(null));
        assertTrue(value(v, "-123.456").toBoolean(null));

        assertTrue(value(v, "2147483647").toBoolean(null));
        assertTrue(value(v, "2147483647L").toBoolean(null));
        assertTrue(value(v, "2147483647F").toBoolean(null));
        assertTrue(value(v, "2147483647D").toBoolean(null));

        assertTrue(value(v, "-2147483648").toBoolean(null));
        assertTrue(value(v, "-2147483648L").toBoolean(null));
        assertTrue(value(v, "-2147483648F").toBoolean(null));
        assertTrue(value(v, "-2147483648D").toBoolean(null));

        assertTrue(value(v, "9223372036854775807").toBoolean(null));
        assertTrue(value(v, "9223372036854775807L").toBoolean(null));
        assertTrue(value(v, "9223372036854775807F").toBoolean(null));
        assertTrue(value(v, "9223372036854775807D").toBoolean(null));

        assertTrue(value(v, "-9223372036854775808").toBoolean(null));
        assertTrue(value(v, "-9223372036854775808L").toBoolean(null));
        assertTrue(value(v, "-9223372036854775808F").toBoolean(null));
        assertTrue(value(v, "-9223372036854775808D").toBoolean(null));

        assertFalse(value(v, "0").toBoolean(null));
        assertFalse(value(v, "0L").toBoolean(null));
        assertFalse(value(v, "0F").toBoolean(null));
        assertFalse(value(v, "0D").toBoolean(null));
        assertFalse(value(v, ".0").toBoolean(null));
        assertFalse(value(v, "0.").toBoolean(null));
        assertFalse(value(v, "0.D").toBoolean(null));
        assertFalse(value(v, "0.0").toBoolean(null));
        assertFalse(value(v, "0x0").toBoolean(null));
        assertFalse(value(v, "0x00").toBoolean(null));
        assertFalse(value(v, "0b00").toBoolean(null));
        assertFalse(value(v, "0.00").toBoolean(null));
        assertFalse(value(v, "false").toBoolean(null));
        assertFalse(value(v, "False").toBoolean(null));
        assertFalse(value(v, "FALSE").toBoolean(null));
        assertFalse(value(v, "00.00").toBoolean(null));
        assertFalse(value(v, "00000000").toBoolean(null));

        assertNull(value(v, "").toBoolean(null));
        assertNull(value(v, "null").toBoolean(null));

        assertTrue(value(v, "0x1").toBoolean(null));
        assertTrue(value(v, "0x8").toBoolean(null));
        assertTrue(value(v, "0xFF").toBoolean(null));
        assertTrue(value(v, "0x8F").toBoolean(null));
        assertTrue(value(v, "0x1234").toBoolean(null));
        assertTrue(value(v, "0x12345678").toBoolean(null));
        assertTrue(value(v, "0x80000000").toBoolean(null));
        assertTrue(value(v, "0x12345678L").toBoolean(null));

        assertThrows(IllegalArgumentException.class, () -> value(v, "NaN").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "FALse").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "Infinity").toBoolean(null));

        assertThrows(IllegalArgumentException.class, () -> value(v, ".").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "+").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-.").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0b").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "+.").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "+D").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x123.0000").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "0x123.4567").toBoolean(null));

        assertThrows(IllegalArgumentException.class, () -> value(v, "-9223372036854775808+").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-9223372036854775808-").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-9223372036854775808A").toBoolean(null));
        assertThrows(IllegalArgumentException.class, () -> value(v, "-9223372036854775808B").toBoolean(null));
    }
}
