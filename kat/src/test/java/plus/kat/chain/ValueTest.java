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
}
